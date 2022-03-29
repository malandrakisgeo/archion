package org.georgemalandrakis.archion.core;

public interface ArchionConstants {

    String FILES_DEFAULT_FILETYPE = "default";
    String FILES_TEMP_FILETYPE = "temporary";
    String FILES_ARCHIVE_FILETYPE = "archive";
    String FILES_TEST_FILETYPE = "test";
    String FILE_UPLOAD_FILED = "upload_failed";

    String FAILED_UPLOAD_MESSAGE = "There was an error uploading the file.";
    Integer FAILED_UPLOAD_MESSAGE_NUM = Integer.valueOf(001);

    String FAILED_UPDATE_MESSAGE = "There was an error updating the file metadata.";
    Integer FAILED_UPDATE_NUM = Integer.valueOf(002);

    String FAILED_DELETION_MESSAGE = "Could not properly delete file: ";
    Integer FAILED_DELETION_NUM = Integer.valueOf(003);

    String FAILED_UPLOAD_TO_CLOUD_INFO = "Could not upload file to cloud service";
    Integer FAILED_UPLOAD_TO_CLOUD_NUM = Integer.valueOf(004);

    String ERROR_SAVING_LOCALLY = "Could not store the file in the local machine";
    Integer ERROR_SAVING_LOCALLY_NUM = Integer.valueOf(005);

    String FAILED_TO_ADD_TO_DB = "Could not save file metadata to DB";
    Integer FAILED_TO_ADD_TO_DB_NUM = Integer.valueOf(006);

    String FAILED_REMOVAL_FROM_CLOUD_INFO = "Could not remove file from cloud service";
    Integer FAILED_REMOVAL_FROM_CLOUD_NUM = Integer.valueOf("007");

    String FAILED_REMOVAL_FROM_DB = "Could not remove file metadata from DB";
    Integer FAILED_REMOVAL_FROM_DB_NUM = Integer.valueOf("008");

    String FAILED_REMOVAL_FROM_LOCAL_MACHINE_INFO = "Could not remove file from local machine";
    Integer FAILED_REMOVAL_FROM_LOCAL_MACHINE_NUM = Integer.valueOf("009");

    String USER_NOT_AUTHORIZED_TO_DELETE_FILE = "The user has no right to delete the file";
    Integer USER_NOT_AUTHORIZED_TO_DELETE_FILE_NUM = Integer.valueOf("010");

    String FILE_READ_ERROR_MESSAGE = "Could not read file.";
    Integer FILE_READ_ERROR_NUM = Integer.valueOf("011");

    String FILE_CREATION_ERROR_MESSAGE = "Could not create file.";
    Integer FILE_CREATION_ERROR_NUM = Integer.valueOf("012");

    String UPLOAD_SUCCESSFUL_MESSAGE = "The test file has been successfully uploaded.";
    String DELETE_SUCCESSFUL_MESSAGE = "The file data has been deleted.";

    String FILE_ALREADY_EXISTS = "The file with the current SHA1 message digest already exists.";

}
