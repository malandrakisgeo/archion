package org.georgemalandrakis.archion.scheduledtasks;

import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.exception.FileDeletionException;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;

import java.util.List;

public class DeleteGeneral {
    FileDAO fileDAO;
    LocalMachineHandler localMachineHandler;
    CloudHandler cloudHandler;

    public DeleteGeneral(FileDAO fileDao, LocalMachineHandler localMachineHandler, CloudHandler cloudHandler) {
        this.fileDAO = fileDao;
        this.localMachineHandler = localMachineHandler;
        this.cloudHandler = cloudHandler;
    }

    public void runDelete(String filetype) {
        if (this.fileDAO != null && this.cloudHandler != null && this.cloudHandler != null) {
            List<FileMetadata> normalFiles = this.fileDAO.fetchOldFiles(filetype);
            normalFiles.forEach(file -> {
                try {
                    if (this.cloudHandler.removeFile(file.getFileid()) && this.localMachineHandler.deleteFile(file.getFileid())) {
                        this.fileDAO.deleteFileById(file.getFileid());
                    } else {
                        throw new FileDeletionException(file.getFileid());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        }
    }
}
