package org.georgemalandrakis.archion.service;

import com.google.common.io.ByteSource;
import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.UserFile;
import org.georgemalandrakis.archion.other.DateUtil;
import org.georgemalandrakis.archion.handlers.CloudHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

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
        UserFile userFile = new UserFile();
        Calendar cal = Calendar.getInstance();

        String originalFilename;
        try {
            originalFilename = filename;
            userFile.setOriginalfilename(originalFilename);
        } catch (Exception ex) {
            ex.printStackTrace();
            archionRequest.getResponseObject().addInformation("error", ex.getMessage());
            return archionRequest;
        }

        userFile.setFileid(UUID.randomUUID().toString());

        switch (purpose) {
            case "temporary": //Temporary files are forcibly deleted after a month by default
                userFile.setOriginalfilename(originalFilename + ".temp");
                cal.add(Calendar.MONTH, 1);
                // temporary files
                userFile.setFiletype(ArchionConstants.FILES_TEMP_FILETYPE);
                break;


            case "archive": //Files marked as "archive" can only be manually deleted.
                userFile.setOriginalfilename(originalFilename);
                userFile.setFiletype(ArchionConstants.FILES_ARCHIVE_FILETYPE);
                break;

            case "test": //Test files are deleted after 5 minutes
                userFile.setOriginalfilename(originalFilename);
                cal.add(Calendar.MINUTE, 5);
                userFile.setFiletype(ArchionConstants.FILES_TEST_FILETYPE);
                break;

            case "save": //To be deleted in a year, if they are not used again in the meanwhile.
            default:
                userFile.setOriginalfilename(originalFilename);
                cal.add(Calendar.YEAR, 1);
                userFile.setFiletype(ArchionConstants.FILES_DEFAULT_FILETYPE);
                break;
        }

        UserFile tempUserFile;

        // Create the database entry, creates a unique local filename -and upload it to the cloud service as well!
        tempUserFile = this.createAndUploadbyInputStream(archionRequest, userFile, new FileInputStream(file));

        if (tempUserFile == null) {
            userFile.setFiletype(ArchionConstants.FILE_UPLOAD_FILED);
            userFile = this.update(archionRequest, userFile);
            archionRequest.getResponseObject().addError("UploadError", ArchionConstants.FAILED_UPLOAD_MESSAGE, ArchionConstants.FAILED_UPLOAD_MESSAGE_NUM);
            return archionRequest;
        }

        userFile.setLastmodified(Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));

        userFile = this.update(archionRequest, tempUserFile);
        if (userFile != null) {
            archionRequest.getResponseObject().addSuccess("Success", ArchionConstants.UPLOAD_SUCCESSFUL_MESSAGE);
        }

        return archionRequest;
    }

    public byte[] getFile(UserFile userFile) {
        byte[] file;
        try {
            file = cloudHandler.downloadFile(String.valueOf(userFile.getFileid()), userFile.getLocalfilename());
        } catch (Exception e) {
            file = null;
            e.printStackTrace();
        }
        return file;
    }


    public UserFile createAndUploadbyInputStream(ArchionRequest archionRequest, UserFile userFile, InputStream fileinputStream) {
        if (userFile != null && fileinputStream != null) {
            userFile = createDBEntry(userFile); //We first store the metadata in the DB, and then the file itself
            if (userFile == null) {
                archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_TO_ADD_TO_DB, ArchionConstants.FAILED_TO_ADD_TO_DB_NUM);
                return null;
            }

            if (localMachineHandler.storeFile(userFile, fileinputStream)) {
                userFile.setPhase(UserFile.Phase.LOCAL_MACHINE); //If something goes wrong when uploading to the cloud, this lets ut know that it was successfully stored in
                // the local machine.

                if (cloudHandler.uploadFile(fileinputStream, userFile.getLocalfilename())) {
                    userFile.setPhase(UserFile.Phase.CLOUD);
                    return userFile;
                } else {
                    archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_UPLOAD_TO_CLOUD_INFO, ArchionConstants.FAILED_UPLOAD_TO_CLOUD_NUM);
                    return null;
                }
            }
            archionRequest.getResponseObject().addError("Error", ArchionConstants.ERROR_SAVING_LOCALLY, ArchionConstants.ERROR_SAVING_LOCALLY_NUM);
        }
        return null;
    }


    public UserFile createDBEntry(UserFile userFile) {
        if (userFile == null) {
            return null;
        }
        userFile.setCreated(Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));
        userFile.setFileextension(getFileExtensionFromFileName(userFile.getOriginalfilename()));
        userFile.setPhase(UserFile.Phase.DB);
        //userFile = fileDao.create(archionRequest, userFile); //TODO: Uncomment
        return userFile;
    }

    /*
        The update function is used in two cases:
        1. To add the timestamp of the creation after the successful creation in the cloud and the local machine
        2. To store information about the procedure phase during which an error occurred (e.g. while uploading to the cloud).
     */
    public UserFile update(ArchionRequest archionRequest, UserFile userFile) {
        try{
            // return fileDao.update(archionRequest, userFile); TODO: Uncomment
        }catch (Exception e){
            e.printStackTrace();
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_UPDATE_MESSAGE, ArchionConstants.FAILED_UPDATE_NUM);
            archionRequest.getResponseObject().addInformation("File: ", userFile.toJSON()); //The application needs info about the file and its' procedure phase

        }
        return userFile;
    }


    public boolean delete(ArchionRequest archionRequest, UserFile userFile) {
        if (archionRequest.getUserObject().getId() != userFile.getFileid()) {//TODO: See if we really need it. The requests are sent
            // by an application and supposedly cannot be manipulated by the user, so such controls may be unnecessary here.
            return (this.removeFileFromSystem(archionRequest, userFile) && fileDao.delete(archionRequest, userFile.getFileid()) > 0); //The metadata will not be removed
            // from the db if the file itself is not removed from the cloud service & the local machine first
        }
        archionRequest.getResponseObject().addError("Error", ArchionConstants.USER_NOT_AUTHORIZED_TO_DELETE_FILE, ArchionConstants.USER_NOT_AUTHORIZED_TO_DELETE_FILE_NUM);
        return false;
    }

    //Removes the file itself from the machine and the cloud service. The removal of its' metadata in the db is another function
    private boolean removeFileFromSystem(ArchionRequest archionRequest, UserFile userFile) {
        if (this.localMachineHandler.deleteFile(userFile.getFileid())) {
            if (this.cloudHandler.removeFile(userFile.getFileid())) {
                return true;
            } else {
                archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_CLOUD_INFO, ArchionConstants.FAILED_REMOVAL_FROM_CLOUD_NUM);
                return false;
            }
        } else {
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_LOCAL_MACHINE_INFO, ArchionConstants.FAILED_REMOVAL_FROM_LOCAL_MACHINE_NUM);
            return false;
        }
    }

    public UserFile retrieveFileMetadata(ArchionRequest archionRequest, String id) {
        return fileDao.retrieve(archionRequest, id);
    }

    public List<UserFile> retrieveList(ArchionRequest archionRequest, String fileType) {
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


}
