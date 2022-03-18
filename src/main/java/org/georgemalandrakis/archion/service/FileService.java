package org.georgemalandrakis.archion.service;

import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.georgemalandrakis.archion.handlers.CloudHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;

public class FileService {
    private final FileDAO fileDao;
    private final CloudHandler cloudHandler;
    private final LocalMachineHandler localMachineHandler;

    public FileService(ConnectionManager connectionObject, FileDAO fileDao, CloudHandler cloudHandler, LocalMachineHandler machineHandler) {
        this.fileDao = fileDao;
        this.cloudHandler = cloudHandler;
        this.localMachineHandler = machineHandler;
    }

    public ArchionRequest createNewFile(ArchionRequest archionRequest, String purpose, String filename, File file) throws Exception {
        FileMetadata fileMetadata = new FileMetadata();
        Calendar cal = Calendar.getInstance();

        String originalFilename;
        try {
            originalFilename = filename;
            fileMetadata.setOriginalfilename(originalFilename);
        } catch (Exception ex) {
            ex.printStackTrace();
            archionRequest.getResponseObject().addInformation("error", ex.getMessage());
            return archionRequest;
        }

        fileMetadata.setFileid(UUID.randomUUID().toString());

        switch (purpose) {
            case "temporary": //Temporary files are forcibly deleted after a month by default
                fileMetadata.setOriginalfilename(originalFilename + ".temp");
                cal.add(Calendar.MONTH, 1);
                // temporary files
                fileMetadata.setFiletype(ArchionConstants.FILES_TEMP_FILETYPE);
                break;

            case "archive": //Files marked as "archive" can only be manually deleted.
                fileMetadata.setOriginalfilename(originalFilename);
                fileMetadata.setFiletype(ArchionConstants.FILES_ARCHIVE_FILETYPE);
                break;

            case "test": //Test files are deleted after 5 minutes
                fileMetadata.setOriginalfilename(originalFilename);
                cal.add(Calendar.MINUTE, 5);
                fileMetadata.setFiletype(ArchionConstants.FILES_TEST_FILETYPE);
                break;

            case "save": //To be deleted in a year, if they are not used again in the meanwhile.
            default:
                fileMetadata.setOriginalfilename(originalFilename);
                cal.add(Calendar.YEAR, 1);
                fileMetadata.setFiletype(ArchionConstants.FILES_DEFAULT_FILETYPE);
                break;
        }



        // Create the database entry, creates a unique local filename -and upload it to the cloud service as well!
        archionRequest = createDBEntry(archionRequest, fileMetadata); //We first store the metadata in the DB, and then the file itself

        if (archionRequest.getResponseObject().getFileMetadata() == null) {
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_TO_ADD_TO_DB, ArchionConstants.FAILED_TO_ADD_TO_DB_NUM);
            return archionRequest;
        }

        archionRequest = this.storeFile(archionRequest, fileMetadata, new FileInputStream(file));

        fileMetadata = archionRequest.getResponseObject().getFileMetadata(); //The metadata is modified in the storeFile() function.

        fileMetadata.setLastmodified(Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));
        archionRequest = this.update(archionRequest, fileMetadata);

        if (archionRequest.getResponseObject().hasError()) {
            fileMetadata.setFiletype(ArchionConstants.FILE_UPLOAD_FILED);
            archionRequest.getResponseObject().addError("UploadError", ArchionConstants.FAILED_UPLOAD_MESSAGE, ArchionConstants.FAILED_UPLOAD_MESSAGE_NUM);
            return archionRequest;
        }

        archionRequest.getResponseObject().addSuccess("Success", ArchionConstants.UPLOAD_SUCCESSFUL_MESSAGE);


        return archionRequest;
    }

    //TODO: We assume here that the metadata used here is up to date. Is it so?
    public byte[] getFile(FileMetadata fileMetadata) {
        byte[] file = null;
        try {
            if (fileMetadata.getPhase().equals(FileProcedurePhase.LOCAL_MACHINE_STORED)) {
                file = localMachineHandler.retrieveFile(fileMetadata.getFileid());
            } else if (fileMetadata.getPhase().equals(FileProcedurePhase.LOCAL_MACHINE_REMOVED)) {
                file = cloudHandler.downloadFile(fileMetadata.getFileid(), fileMetadata.getLocalfilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public ArchionRequest storeFile(ArchionRequest archionRequest, FileMetadata fileMetadata, InputStream fileinputStream) {
        if (fileMetadata != null && fileinputStream != null) {


            if (localMachineHandler.storeFile(fileMetadata, fileinputStream)) {
                fileMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_STORED); //If something goes wrong when uploading to the cloud, this lets ut know that it was successfully stored in
                // the local machine.

                if (cloudHandler.uploadFile(fileinputStream, fileMetadata.getLocalfilename())) {
                    fileMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_STORED);
                } else {
                    archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_UPLOAD_TO_CLOUD_INFO, ArchionConstants.FAILED_UPLOAD_TO_CLOUD_NUM);
                    archionRequest.getResponseObject().addInformation("Info", fileMetadata.toJSON());
                }
            }
            archionRequest.getResponseObject().addError("Error", ArchionConstants.ERROR_SAVING_LOCALLY, ArchionConstants.ERROR_SAVING_LOCALLY_NUM);
            archionRequest.getResponseObject().setFileMetadata(fileMetadata);
        }
        return archionRequest;

    }


    public ArchionRequest createDBEntry(ArchionRequest archionRequest, FileMetadata fileMetadata) {
        if (fileMetadata == null) {
            return null;
        }
        fileMetadata.setCreated(Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));
        fileMetadata.setFileextension(getFileExtensionFromFileName(fileMetadata.getOriginalfilename()));
        fileMetadata.setPhase(FileProcedurePhase.DB_METADATA_STORED);
        //fileMetadata = fileDao.create(archionRequest, fileMetadata); //TODO: Uncomment
        archionRequest.getResponseObject().setFileMetadata(fileMetadata);
        return archionRequest;
    }

    /*
        The update function is used in two cases:
        1. To add the timestamp of the creation after the successful creation in the cloud and the local machine
        2. To store information about the procedure phase during which an error occurred (e.g. while uploading to the cloud).
     */
    public ArchionRequest update(ArchionRequest archionRequest, FileMetadata fileMetadata) { //The metadata may be different from the one of the request
        //TODO: If there is some error when creating the db entry, there will be a second error here too.
        try {
            // fileMetadata = fileDao.update(archionRequest, fileMetadata); TODO: Uncomment
        } catch (Exception e) {
            e.printStackTrace();
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_UPDATE_MESSAGE, ArchionConstants.FAILED_UPDATE_NUM);
            archionRequest.getResponseObject().addInformation("File: ", archionRequest.getInitialMetadata().toJSON()); //The application needs info about the file and its' procedure phase

        }
        archionRequest.getResponseObject().setFileMetadata(archionRequest.getInitialMetadata());
        return archionRequest;
    }


    public ArchionRequest deletePermanently(ArchionRequest archionRequest) {
        ArchionRequest archreqModified = archionRequest;

        if (archionRequest.getUserObject().getId() != archionRequest.getInitialMetadata().getFileid()) {//TODO: See if we really need it. The requests are sent
            // by an application and supposedly cannot be manipulated by the user, so such controls may be unnecessary here.

            // The metadata shall not be removed from the db if the file itself is not removed from the cloud service & the local machine first.
            archreqModified = this.removeFileFromSystem(archreqModified, archionRequest.getInitialMetadata());
            if (archreqModified.getResponseObject().hasError()) {
                return archreqModified;
            } else {
                return this.fileDao.deletePermanently(archionRequest, archionRequest.getInitialMetadata());
            }

        }
        archionRequest.getResponseObject().addError("Error", ArchionConstants.USER_NOT_AUTHORIZED_TO_DELETE_FILE, ArchionConstants.USER_NOT_AUTHORIZED_TO_DELETE_FILE_NUM);
        return archreqModified;
    }

    //Removes the file itself from the machine and the cloud service. The removal of its' metadata in the db is another function
    private ArchionRequest removeFileFromSystem(ArchionRequest archionRequest, FileMetadata fileMetadata) {
        if (fileMetadata.getPhase() != FileProcedurePhase.LOCAL_MACHINE_REMOVED) { //It is possible for a file available in the web service to be already removed from the local
            // machine, but not the opposite.
            if (this.localMachineHandler.deleteFile(fileMetadata.getFileid())) {
                fileMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_REMOVED);
            } else {
                archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_LOCAL_MACHINE_INFO, ArchionConstants.FAILED_REMOVAL_FROM_LOCAL_MACHINE_NUM);
                archionRequest.getResponseObject().addInformation("Info", fileMetadata.toJSON());
                return archionRequest;
            }
        }

        if (this.cloudHandler.removeFile(fileMetadata.getFileid())) {
            fileMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_REMOVED);
        } else {
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_CLOUD_INFO, ArchionConstants.FAILED_REMOVAL_FROM_CLOUD_NUM);
            archionRequest.getResponseObject().addInformation("Info", fileMetadata.toJSON());
        }
        return archionRequest;

    }

    public FileMetadata retrieveFileMetadata(ArchionRequest archionRequest, String id) {
        return fileDao.retrieve(archionRequest, id);
    }

    public List<FileMetadata> retrieveList(ArchionRequest archionRequest, String fileType) {
        return fileDao.retrieveList(archionRequest, fileType);
    }

    private String getFileExtensionFromFileName(String fileName) {
        String fileExtension = "";

        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            fileExtension = fileName.substring(i + 1);
        }

        return fileExtension;
    }

    private ArchionRequest calculateDigests(ArchionRequest archionRequest, File file){
        CRC32 crc = new CRC32();
//TODO: fix
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            crc.update(fileInputStream.readAllBytes());
            archionRequest.getResponseObject().getFileMetadata().setCrc32Hex(crc.toString());
        } catch (Exception e ) {
            e.printStackTrace();
        }

        return archionRequest;
    }
}
