package org.georgemalandrakis.archion.dao;

import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.mapper.UserFileMapper;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.core.ArchionNotification;
import org.georgemalandrakis.archion.core.ArchionRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileDAO extends AbstractDAO {
    ConnectionManager connectionObject;
    Connection connection;

    public FileDAO(ConnectionManager connectionObject) {
        super(connectionObject);
        this.connectionObject = connectionObject;

    }


    public FileMetadata create(ArchionRequest archionRequest, FileMetadata fileMetadata) {

        try {
            connection = this.getConnection();
            Integer triesLeft = 5;


            String newLocalFilename = (fileMetadata.getLocalfilename() != null ? fileMetadata.getLocalfilename() : fileMetadata.getOriginalfilename() + "-" + fileMetadata.getFileid());
            fileMetadata.setLocalfilename(newLocalFilename);


            PreparedStatement statement = connection.prepareStatement("INSERT INTO files (userid, originalfilename, localfilename,location, fileextension, sizeinkbs, fileid) VALUES (?, ?, ?,?,?,?,?)");
            statement.setObject(1, java.util.UUID.fromString((fileMetadata.getUserid() != null ? fileMetadata.getUserid() : archionRequest.getUserObject().getId().toString())));
            statement.setString(2, fileMetadata.getOriginalfilename());
            statement.setString(3, fileMetadata.getLocalfilename() == null ? newLocalFilename : fileMetadata.getLocalfilename());
            statement.setString(4, fileMetadata.getLocation());
            statement.setString(5, fileMetadata.getFileextension());
            statement.setString(6, fileMetadata.getSizeinkbs());
            statement.setObject(7, java.util.UUID.fromString(fileMetadata.getFileid()));

            Integer results = statement.executeUpdate(); //Avoids an ""PSQLException: No results were returned by the query."

            if (results < 0) {
                archionRequest.getResponseObject().addError("SQL Error", ArchionConstants.FILE_CREATION_ERROR_MESSAGE);
                return null;
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
            archionRequest.getResponseObject().addError("SQL Error", ArchionConstants.FILE_CREATION_ERROR_MESSAGE);
            return null;
        }
        return fileMetadata;
    }

    public FileMetadata retrieve(ArchionRequest archionRequest, String id) {
        FileMetadata userile = null;

        try {
            connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT  * FROM files WHERE fileid = ?");
            statement.setObject(1, java.util.UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userile = UserFileMapper.map(resultSet);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            if(archionRequest!=null){
                archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
            }
        }

        return userile;

    }


    public List<FileMetadata> retrieveList(ArchionRequest archionRequest, String filetype) {
        List<FileMetadata> fileList = new ArrayList<>();
        FileMetadata usf;

        try {
            connection = this.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT  * FROM files WHERE filetype = ? AND ownerid = ?");

            statement.setObject(1, java.util.UUID.fromString(filetype));
            statement.setObject(2, archionRequest.getUserObject().getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                usf = UserFileMapper.map(resultSet);
                fileList.add(usf);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
        }

        return fileList;

    }


    public List<FileMetadata> retrieveAll(ArchionRequest archionRequest) {
        List<FileMetadata> files = new ArrayList<>();

        try {
            connection = this.getConnection();
            String sql = "SELECT * from  files WHERE uploader =" + archionRequest.getUserObject().getId() + " ;";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                files.add(UserFileMapper.map(resultSet));
            }

        } catch (SQLException sqlException) {
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
        }

        return files;
    }

    public List<FileMetadata> fetchOldFiles(String filetype) {
        List<FileMetadata> fileList = new ArrayList<>();
        FileMetadata usf;
        Calendar calendar = java.util.Calendar.getInstance();
        if (filetype.equalsIgnoreCase(ArchionConstants.FILES_TEMP_FILETYPE)) {
            calendar.add(Calendar.MONTH, -1);

        } else if (filetype.equalsIgnoreCase(ArchionConstants.FILES_TEST_FILETYPE)) {
            calendar.add(Calendar.MINUTE, -5);
        } else if (filetype.equalsIgnoreCase(ArchionConstants.FILES_DEFAULT_FILETYPE)) {
            calendar.add(Calendar.YEAR, -1); //One year old files are regarded as old
        } else {
            return null;
        }
/* //TODO: Uncomment
        try {
            connection = this.getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT  fileid, localfilename FROM files WHERE filetype = ? AND lastmodified < ?");
            statement.setString(1, filetype);
            statement.setTimestamp(2, Timestamp.valueOf(String.valueOf(calendar.getTimeInMillis())));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                usf = UserFileMapper.map(resultSet);
                fileList.add(usf);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
*/
        return fileList;
    }

    public FileMetadata update(ArchionRequest archionRequest, FileMetadata fileMetadata) {

        Integer count = 0;
        try {
            connection = this.getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE files SET created = ? WHERE fileid = ?");
            statement.setTimestamp(1, fileMetadata.getCreated());
            statement.setObject(2, java.util.UUID.fromString(fileMetadata.getFileid()));

            count = statement.executeUpdate();
        } catch (SQLException sqlException) {
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
        }

        if (count > 0 && !archionRequest.getResponseObject().hasType(ArchionNotification.NotificationType.error)) {
            return this.retrieve(archionRequest, fileMetadata.getFileid());
        }
        return null;
    }

    public ArchionRequest deletePermanently(ArchionRequest archionRequest, FileMetadata fileMetadata) {

        Integer retVal = 0;

        try {
            connection = this.getConnection();
            retVal = this.deleteFileById(fileMetadata.getFileid());

        } catch (SQLException sqlException) {
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_DB);
            archionRequest.getResponseObject().addInformation("Info", fileMetadata.toJSON());
        }

        if (retVal > 0 && !archionRequest.getResponseObject().hasError()) {
            archionRequest.getResponseObject().addSuccess("Success", ArchionConstants.DELETE_SUCCESSFUL_MESSAGE);
            archionRequest.getResponseObject().setFileMetadata(null);
        }

        return archionRequest;
    }

    public Integer deleteFileById(String id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM files WHERE fileid = ?");
        statement.setObject(1, java.util.UUID.fromString(id));
        return statement.executeUpdate();
    }


}
