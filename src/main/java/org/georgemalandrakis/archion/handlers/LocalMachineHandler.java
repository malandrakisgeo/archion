package org.georgemalandrakis.archion.handlers;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;

import java.io.*;

public class LocalMachineHandler {
    String localFileFolder;

    public LocalMachineHandler(ConnectionManager connectionObject) {
        localFileFolder = connectionObject.getLocalMachineFolder();
    }

    public byte[] retrieveFile(String id) {
        return null;
    }

    public FileMetadata storeFile(FileMetadata fileMetadata, InputStream data) {
        String fileLocationAndName = this.localFileFolder + "-" + fileMetadata.getFileid();
        try {
            File file = new File(fileLocationAndName);
            OutputStream outStream = new FileOutputStream(file);
            outStream.write(data.readAllBytes()); //Java 9 TODO:fix
            //outStream.flush(); //TODO:fix
            outStream.close();
            fileMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_STORED);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return fileMetadata;
    }


    public FileMetadata deleteFile(FileMetadata fileMetadata){
        //TODO implement
        /*
            Null if unsuccessful.

         */
        if(fileMetadata.getPhase() == FileProcedurePhase.LOCAL_MACHINE_STORED){
            fileMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_STORED);
        }
        return fileMetadata;
    }

}
