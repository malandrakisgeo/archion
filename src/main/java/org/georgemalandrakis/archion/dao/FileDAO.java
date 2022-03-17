package org.georgemalandrakis.archion.dao;

import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.mapper.UserFileMapper;
import org.georgemalandrakis.archion.model.UserFile;
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


    public UserFile create(ArchionRequest archionRequest, UserFile userFile) {

        try {
            connection = this.getConnection();
            Integer triesLeft = 5;


            String newLocalFilename = (userFile.getLocalfilename() != null ? userFile.getLocalfilename() : userFile.getOriginalfilename() + "-" + userFile.getFileid());
            userFile.setLocalfilename(newLocalFilename);


            PreparedStatement statement = connection.prepareStatement("INSERT INTO files (userid, originalfilename, localfilename,location, fileextension, sizeinkbs, fileid) VALUES (?, ?, ?,?,?,?,?)");
            statement.setObject(1, java.util.UUID.fromString((userFile.getUserid() != null ? userFile.getUserid() : archionRequest.getUserObject().getId().toString())));
            statement.setString(2, userFile.getOriginalfilename());
            statement.setString(3, userFile.getLocalfilename() == null ? newLocalFilename : userFile.getLocalfilename());
            statement.setString(4, userFile.getLocation());
            statement.setString(5, userFile.getFileextension());
            statement.setString(6, userFile.getSizeinkbs());
            statement.setObject(7, java.util.UUID.fromString(userFile.getFileid()));

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
        return userFile;
    }

    public UserFile retrieve(ArchionRequest archionRequest, String id) {
        UserFile userile = null;

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
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
        }

        return userile;

    }


    public List<UserFile> retrieveList(ArchionRequest archionRequest, String filetype) {
        List<UserFile> fileList = new ArrayList<>();
        UserFile usf;

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


    public List<UserFile> retrieveAll(ArchionRequest archionRequest) {
        List<UserFile> files = new ArrayList<>();

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

    public List<UserFile> fetchOldFiles(String filetype) {
        List<UserFile> fileList = new ArrayList<>();
        UserFile usf;
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
/*
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

    public UserFile update(ArchionRequest archionRequest, UserFile userFile) {

        Integer count = 0;
        try {
            connection = this.getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE files SET created = ? WHERE fileid = ?");
            statement.setTimestamp(1, userFile.getCreated());
            statement.setObject(2, java.util.UUID.fromString(userFile.getFileid()));

            count = statement.executeUpdate();
        } catch (SQLException sqlException) {
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
        }

        if (count > 0 && !archionRequest.getResponseObject().hasType(ArchionNotification.NotificationType.error)) {
            return this.retrieve(archionRequest, userFile.getFileid());
        }
        return null;
    }

    public Integer delete(ArchionRequest archionRequest, String id) {

        Integer retVal = 0;

        try {
            connection = this.getConnection();
            retVal = this.deleteFileById(id);

        } catch (SQLException sqlException) {
            archionRequest.getResponseObject().addDebug("SQLException", sqlException.getMessage());
            archionRequest.getResponseObject().addError("Error", ArchionConstants.FAILED_REMOVAL_FROM_DB);
        }

        if (retVal > 0 && !archionRequest.getResponseObject().hasType(ArchionNotification.NotificationType.error)) {
            archionRequest.getResponseObject().addSuccess("Success", ArchionConstants.DELETE_SUCCESSFUL_MESSAGE);
        }

        return retVal;
    }

    public Integer deleteFileById(String id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM files WHERE fileid = ?");
        statement.setObject(1, java.util.UUID.fromString(id));
        return statement.executeUpdate();
    }


}
