package org.georgemalandrakis.archion.scheduledtasks;

import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.exception.FileDeletionException;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;

import java.sql.SQLException;
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
        if (this.fileDAO != null && this.localMachineHandler != null && this.cloudHandler != null) {
            List<FileMetadata> normalFiles = this.fileDAO.fetchOldFiles(filetype);
            normalFiles.forEach(file -> {
                try {
                    this.remove(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void removeDuplicates() {
        if (this.fileDAO != null && this.localMachineHandler != null && this.cloudHandler != null) {
            List<FileMetadata> normalFiles = this.fileDAO.fetchUserDuplicates();
            normalFiles.forEach(file -> {
                try {
                    this.remove(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void cleanLocalMachine() {
        if (this.fileDAO != null && this.localMachineHandler != null && this.cloudHandler != null) {
            List<FileMetadata> normalFiles = this.fileDAO.fetchNotAccessedForThreeDays();
            normalFiles.forEach(file -> {
                try {
                    FileMetadata tempMetadata = this.localMachineHandler.deleteFileFromLocalMachine(file);
                    if(tempMetadata!=null){
                        this.fileDAO.update(null, tempMetadata);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void remove(FileMetadata file) throws Exception {
        FileMetadata tempFileMetadata;
        file.getFileid();

        if (file.getPhase() == FileProcedurePhase.LOCAL_MACHINE_STORED  || file.getPhase() == FileProcedurePhase.CLOUD_SERVICE_STORED ) {
            tempFileMetadata = this.localMachineHandler.deleteFileFromLocalMachine(file);
            if (tempFileMetadata == null) {
                throw new FileDeletionException(file);
            } else {
                file = tempFileMetadata;
            }
        }

        if (file.getPhase() == FileProcedurePhase.LOCAL_MACHINE_REMOVED || file.getPhase() == FileProcedurePhase.CLOUD_SERVICE_STORED ) {
            // the machine in the first place
            if (this.cloudHandler.removeFile(file) == null) {
                throw new FileDeletionException(file);
            }
        }

        try {
            this.fileDAO.deleteFileById(file.getFileid());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new FileDeletionException(file);
        }
    }

}
