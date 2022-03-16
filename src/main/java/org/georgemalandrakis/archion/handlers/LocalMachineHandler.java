package org.georgemalandrakis.archion.handlers;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.model.UserFile;
import redis.clients.jedis.util.IOUtils;

import java.io.*;

public class LocalMachineHandler {
    String localFileFolder;

    public LocalMachineHandler(ConnectionManager connectionObject) {
        localFileFolder = connectionObject.getLocalMachineFolder();
    }

    public File retrieveFile(String id) {
        return null;
    }

    public boolean storeFile(UserFile userFile, InputStream data) {
        String fileLocationAndName = this.localFileFolder + "-" + userFile.getOriginalfilename() + "-" + userFile.getFileid();
        try {
            File file = new File(fileLocationAndName);
            OutputStream outStream = new FileOutputStream(file);
            outStream.write(data.readAllBytes()); //Java 9 TODO:fix
            //outStream.flush(); //TODO:fix
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean deleteFile(String id) {
        return null;
    }

}
