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

    public ArchionRequest createNewFile(ArchionRequest archionRequest, String purpose, String filename, File file) throws Exception{
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
                userFile.setOriginalfilename(originalFilename+".temp");
                cal.add(Calendar.MONTH,1);
                // temporary files
                userFile.setFiletype(ArchionConstants.FILES_TEMP_FILETYPE);
                break;


            case "archive": //Files marked as "archive" can only be manually deleted.
                userFile.setOriginalfilename(originalFilename);
                userFile.setFiletype(ArchionConstants.FILES_ARCHIVE_FILETYPE);
                break;

            case "test": //Test files are deleted after 5 minutes
                userFile.setOriginalfilename(originalFilename);
                cal.add(Calendar.MINUTE,5);
                userFile.setFiletype(ArchionConstants.FILES_TEST_FILETYPE);
                break;

            case "save": //To be deleted in a year, if they are not used again in the meanwhile.
            default:
                userFile.setOriginalfilename(originalFilename);
                cal.add(Calendar.YEAR,1);
                userFile.setFiletype(ArchionConstants.FILES_DEFAULT_FILETYPE);
                break;        }

        UserFile tempUserFile;

        // Create the database entry, creates a unique local filename -and upload it to the cloud service as well!
        tempUserFile = this.createAndUploadbyInputStream(archionRequest, userFile, new FileInputStream(file));

        if (tempUserFile == null) {
            userFile.setFiletype(ArchionConstants.FILED_UPLOAD_FILED);
            userFile = this.update(archionRequest, userFile);
            archionRequest.getResponseObject().addError("UploadError", ArchionConstants.FAILED_UPLOAD_MESSAGE);
            return archionRequest;
            //throw new FileDBException();
        }

        // If we got this far, we successfully stored the file. YAY!


        userFile.setLastmodified(Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));

        userFile = this.update(archionRequest, tempUserFile);
        if (userFile != null) {
            archionRequest.getResponseObject().addSuccess("Success", ArchionConstants.UPLOAD_SUCCESSFUL_MESSAGE);
        } else {
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_UPDATE_MESSAGE);
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
            userFile = createDBEntry(userFile);

            if (cloudHandler.uploadFile(fileinputStream, userFile.getLocalfilename()) && localMachineHandler.storeFile(userFile, fileinputStream)) {
                return userFile;
            }
        }
        return null;
    }



    public UserFile createDBEntry(UserFile userFile) {
        if (userFile == null) {
            return null;
        }
        userFile.setCreated(Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));
        userFile.setFileextension(getFileExtensionFromFileName(userFile.getOriginalfilename()));
        //userFile = fileDao.create(archionRequest, userFile); //TODO: Uncomment
        return userFile;
    }

    public UserFile update(ArchionRequest archionRequest, UserFile userFile) {
       // return fileDao.update(archionRequest, userFile); TODO: Uncomment
        return userFile;
    }



    public Integer delete(ArchionRequest archionRequest, UserFile userFile) {
        return fileDao.delete(archionRequest, userFile.getFileid());
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
