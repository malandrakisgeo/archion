package org.georgemalandrakis.archion.handlers;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.exception.FileDeletionException;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.georgemalandrakis.archion.other.FileUtil;

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
        String fileLocationAndName = this.localFileFolder + fileMetadata.getFileid();
        try {
            File file = FileUtil.createFile(fileLocationAndName);
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

    //This function MUST NOT run for files stored only in the cloud or the db.
    public FileMetadata deleteFileFromLocalMachine(FileMetadata fileMetadata){

        if(!(fileMetadata.getPhase() == FileProcedurePhase.LOCAL_MACHINE_STORED  || fileMetadata.getPhase() == FileProcedurePhase.CLOUD_SERVICE_STORED)){
            return null;
        }

        String fileLocationAndName = this.localFileFolder + fileMetadata.getFileid();
        try {
            File file = FileUtil.createFile(fileLocationAndName);
            boolean success = file.delete();
            if(success){
                fileMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_REMOVED);
            }else{
                throw new FileDeletionException(fileMetadata);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return fileMetadata;
    }



}
