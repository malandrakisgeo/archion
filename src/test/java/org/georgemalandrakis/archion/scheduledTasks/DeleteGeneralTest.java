package org.georgemalandrakis.archion.scheduledTasks;

import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.exception.FileDeletionException;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.georgemalandrakis.archion.scheduledtasks.DeleteGeneral;
import org.georgemalandrakis.archion.service.FileService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class DeleteGeneralTest {


    @Mock
    CloudHandler cloudHandler;

    @Mock
    FileDAO fileDAO;

    @Mock
    LocalMachineHandler localMachineHandler;

    @InjectMocks
    DeleteGeneral deleteGeneral;


    @BeforeTest
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test(expectedExceptions = FileDeletionException.class)
    public void throws_FileDeletionException_if_deleteUnsuccessful() throws Exception{
        when(localMachineHandler.deleteFileFromLocalMachine(any())).thenReturn(null);

        deleteGeneral.removeTest(this.normalFiles(FileProcedurePhase.LOCAL_MACHINE_STORED).get(0));
        //throw new FileDeletionException(this.normalFiles().get(0));

    }

    @Test
    public void localMachineHandler_willNotRun_ifFileNotInLocalMachine() throws Exception{
        when(cloudHandler.removeFile(any())).thenReturn(this.normalFiles(FileProcedurePhase.CLOUD_SERVICE_REMOVED).get(0));

        deleteGeneral.removeTest(this.normalFiles(FileProcedurePhase.LOCAL_MACHINE_REMOVED).get(0));
        Mockito.verify(localMachineHandler, times(0)).deleteFileFromLocalMachine(any());

    }


    List<FileMetadata> normalFiles(FileProcedurePhase fileProcedurePhase){
        FileMetadata file1 = new FileMetadata();
        FileMetadata file2 = new FileMetadata();

        file1.setFileid(UUID.randomUUID().toString());
        file1.setOriginalfilename("filename");
        file1.setPhase(fileProcedurePhase);

        file2.setFileid(UUID.randomUUID().toString());
        file2.setOriginalfilename("filename2");
        file2.setPhase(fileProcedurePhase);

        List<FileMetadata> fileMetadataList = new ArrayList<>();
        fileMetadataList.add(file1);
        fileMetadataList.add(file2);

        return fileMetadataList;
    }

}
