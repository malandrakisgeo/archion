package org.georgemalandrakis.archion.handlers;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.model.FileMetadata;

import java.io.*;

public class LocalMachineHandler {
    String localFileFolder;

    public LocalMachineHandler(ConnectionManager connectionObject) {
        localFileFolder = connectionObject.getLocalMachineFolder();
    }

    public byte[] retrieveFile(String id) {
        return null;
    }

    public boolean storeFile(FileMetadata fileMetadata, InputStream data) {
        String fileLocationAndName = this.localFileFolder + "-" + fileMetadata.getOriginalfilename() + "-" + fileMetadata.getFileid();
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
        /*
            if file not found, return true!
            It is assumed that it already was deleted by some automated procedure
         */
        return null; //TODO implement
    }

}
