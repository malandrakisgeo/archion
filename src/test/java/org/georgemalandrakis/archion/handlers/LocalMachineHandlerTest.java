package org.georgemalandrakis.archion.handlers;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.georgemalandrakis.archion.other.FileUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//@RunWith(PowerMockRunner.class)
public class LocalMachineHandlerTest {

    @Mock
    ConnectionManager connectionManager;

    @Mock
    File file;

    @InjectMocks
    LocalMachineHandler testObj;

    FileMetadata sampleMetadata;


    @BeforeTest
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        when(connectionManager.getLocalMachineFolder()).thenReturn("testMachine");
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


    @Test
    public void phase_LocalMachineRemoved_after_deleting() throws Exception {
        FileMetadata fileMetadata;

        sampleMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_STORED);

        //Mocking a static method
        try (MockedStatic<FileUtil> utilities = mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.createFile(anyString())).thenReturn(file);
            when(file.delete()).thenReturn(true);
            fileMetadata = testObj.deleteFileFromLocalMachine(sampleMetadata);
        }

        assert(fileMetadata != null);
        assert(fileMetadata.getPhase() == FileProcedurePhase.LOCAL_MACHINE_REMOVED);

    }


    @Test
    public void null_when_deleteUnsuccessful() {
        FileMetadata fileMetadata;

        sampleMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_STORED);

        //Mocking a static method
        try (MockedStatic<FileUtil> utilities = mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.createFile(anyString())).thenReturn(file);
            when(file.delete()).thenReturn(false);
            fileMetadata = testObj.deleteFileFromLocalMachine(sampleMetadata);
        }

        assert(fileMetadata==null);
    }


    private void setSampleMetadata(){
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFileid(UUID.randomUUID().toString());
        fileMetadata.setUserid(UUID.randomUUID().toString());
        fileMetadata.setOriginalfilename("testfile");

        this.sampleMetadata = fileMetadata;
    }



}
