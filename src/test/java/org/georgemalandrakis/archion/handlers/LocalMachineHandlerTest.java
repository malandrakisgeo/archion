package org.georgemalandrakis.archion.handlers;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.UUID;

public class LocalMachineHandlerTest {

    @Mock
    ConnectionManager connectionManager;

    @InjectMocks
    LocalMachineHandler testObj;

    FileMetadata sampleMetadata;


    @BeforeTest
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(connectionManager.getLocalMachineFolder()).thenReturn("testMachine");
        this.setSampleMetadata();

    }

    @Test
    public void returnNull_when_fileMetadata_Not_LocalMachineOrCloud(){
        sampleMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_REMOVED);
        Object returned = testObj.deleteFileFromLocalMachine(sampleMetadata);

        sampleMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_REMOVED);
        Object returned2 = testObj.deleteFileFromLocalMachine(sampleMetadata);

        assert(returned == null);
        assert(returned2 == null);

    }


    private void setSampleMetadata(){
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFileid(UUID.randomUUID().toString());
        fileMetadata.setUserid(UUID.randomUUID().toString());
        fileMetadata.setOriginalfilename("testfile");

        this.sampleMetadata = fileMetadata;
    }



}
