package org.georgemalandrakis.archion.service;

import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.other.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import static org.georgemalandrakis.archion.other.FileUtil.getFileExtensionFromFileName;

public class FileService {
    private final FileDAO fileDao;
    private final CloudHandler cloudHandler;
    private final LocalMachineHandler localMachineHandler;

    public FileService(ConnectionManager connectionObject, FileDAO fileDao, CloudHandler cloudHandler, LocalMachineHandler machineHandler) {
        this.fileDao = fileDao;
        this.cloudHandler = cloudHandler;
        this.localMachineHandler = machineHandler;
    }

    public FileMetadata getUpdatedMetadata(String fileId) {
        return this.fileDao.retrieve(null, fileId);
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
        fileMetadata.setSizeinkbs(String.valueOf(Files.size(file.toPath()) / 1024));
        fileMetadata.setSha1Hash(FileUtil.calculate_SHA1(new FileInputStream(file)));


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

        fileMetadata.setLastmodified(new Timestamp(System.currentTimeMillis()));
        archionRequest = this.update(archionRequest, fileMetadata);

        if (archionRequest.getResponseObject().hasError()) {
            fileMetadata.setFiletype(ArchionConstants.FILE_UPLOAD_FILED);
            archionRequest.getResponseObject().addError("UploadError", ArchionConstants.FAILED_UPLOAD_MESSAGE, ArchionConstants.FAILED_UPLOAD_MESSAGE_NUM);
            return archionRequest;
        }

        archionRequest.getResponseObject().addSuccess("Success", ArchionConstants.UPLOAD_SUCCESSFUL_MESSAGE);
        archionRequest.getResponseObject().setFileMetadata(fileMetadata);


        return archionRequest;
    }

    public byte[] getFile(FileMetadata fileMetadata) {
        //NOTE: Make sure that the FileMetadata is up-to-date before calling this function
        byte[] file = null;
        boolean resaveLocally = false;
        fileMetadata.setLastAccessed(new Timestamp(System.currentTimeMillis()));

        try {
            if (fileMetadata.getPhase().equals(FileProcedurePhase.LOCAL_MACHINE_STORED)) {
                file = localMachineHandler.retrieveFile(fileMetadata.getFileid()); //Retrieve from local machine if recently accessed.
            } else if (fileMetadata.getPhase().equals(FileProcedurePhase.CLOUD_SERVICE_STORED)) {
                file = cloudHandler.downloadFile(fileMetadata.getFileid());
                resaveLocally = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resaveLocally) {
            fileMetadata = resaveInLocalMachine(fileMetadata, file); //User need not be notified if something goes wrong here.
        }

        fileDao.updateLastAccessed(fileMetadata.getFileid()); //TODO: Add some form of error check here

        return file;
    }

    private FileMetadata resaveInLocalMachine(FileMetadata fileMetadata, byte[] filebytes) {
        //TODO: Implement
        return null;
    }

    private ArchionRequest storeFile(ArchionRequest archionRequest, FileMetadata fileMetadata, InputStream fileinputStream) {
        if (fileMetadata != null && fileinputStream != null) {
            if (localMachineHandler.storeFile(fileMetadata, fileinputStream) != null) { //If file successfully saved in local machine, try to save in cloud as well
                if (cloudHandler.uploadFile(fileinputStream, fileMetadata) == null) { //and if anything goes wrong while uploading
                    //inform the user
                    archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_UPLOAD_TO_CLOUD_INFO, ArchionConstants.FAILED_UPLOAD_TO_CLOUD_NUM);
                    archionRequest.getResponseObject().addInformation("Info", fileMetadata.toJSON());
                }
            } else {
                archionRequest.getResponseObject().addError("Error", ArchionConstants.ERROR_SAVING_LOCALLY, ArchionConstants.ERROR_SAVING_LOCALLY_NUM);
            }
            archionRequest.getResponseObject().setFileMetadata(fileMetadata);
        }
        return archionRequest;

    }


    private ArchionRequest createDBEntry(ArchionRequest archionRequest, FileMetadata fileMetadata) {
        if (fileMetadata == null) {
            return null;
        }
        Timestamp creationandFirstAccess = new Timestamp(System.currentTimeMillis());
        fileMetadata.setCreated(creationandFirstAccess);
        fileMetadata.setLastAccessed(creationandFirstAccess);
        fileMetadata.setFileextension(getFileExtensionFromFileName(fileMetadata.getOriginalfilename()));
        fileMetadata.setPhase(FileProcedurePhase.DB_METADATA_STORED);
        fileMetadata.setUserid(archionRequest.getUserObject().getId());
        String str = archionRequest.getUserObject().getId();
        str.hashCode();
        fileMetadata = fileDao.create(archionRequest, fileMetadata); //TODO: Uncomment
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


    public FileMetadata retrieveFileMetadata(ArchionRequest archionRequest, String id) {
        return fileDao.retrieve(archionRequest, id);
    }

    public List<FileMetadata> retrieveList(ArchionRequest archionRequest, String fileType) {
        return fileDao.retrieveList(archionRequest, fileType);
    }


    public ArchionRequest remove(ArchionRequest archionRequest) {
        FileMetadata file = archionRequest.getInitialMetadata();
        FileMetadata tempFileMetadata;

        if (file.getPhase() == FileProcedurePhase.LOCAL_MACHINE_STORED) {
            tempFileMetadata = this.localMachineHandler.deleteFile(file);
            if (tempFileMetadata == null) {
                archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_LOCAL_MACHINE_INFO, ArchionConstants.FAILED_REMOVAL_FROM_LOCAL_MACHINE_NUM);
                archionRequest.getResponseObject().addInformation("Info", file.toJSON());
            } else {
                file = tempFileMetadata;
            }
        }

        if (file.getPhase() == FileProcedurePhase.CLOUD_SERVICE_STORED) { //If successfully removed now or already removed from the machine in the first place
            if (this.cloudHandler.removeFile(file) == null) {
                archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_CLOUD_INFO, ArchionConstants.FAILED_REMOVAL_FROM_CLOUD_NUM);
                archionRequest.getResponseObject().addInformation("Info", file.toJSON());
            }
        }

        try {
            this.fileDao.deleteFileById(file.getFileid());
        } catch (SQLException e) {
            e.printStackTrace();
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_DB, ArchionConstants.FAILED_REMOVAL_FROM_DB_NUM);
            archionRequest.getResponseObject().addInformation("Info", file.toJSON());
        }

        return archionRequest;
    }
}
