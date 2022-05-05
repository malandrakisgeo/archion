package org.georgemalandrakis.archion.service;

import org.georgemalandrakis.archion.core.*;
import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;
import org.georgemalandrakis.archion.other.FileUtil;
import org.junit.Before;
import org.mockito.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    @Mock
    ConnectionManager connectionManager;

    @Mock
    CloudHandler cloudHandler;

    @Mock
    FileDAO fileDAO;

    @Mock
    File file;

    @Mock
    LocalMachineHandler localMachineHandler;

    @InjectMocks
    FileService fileService;

    private ArchionRequest archionRequest;


    @BeforeTest
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        when(connectionManager.getLocalMachineFolder()).thenReturn("testMachine");
    }


    @Test
    public void noCreation_if_fileAlreadyExists() throws Exception {
        ArchionRequest returned;
        this.setSampleRequest();

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            MockedStatic<FileUtil> fUtil = mockStatic(FileUtil.class);
            files.when(() -> Files.size(any())).thenReturn(Long.valueOf(10000));
            fUtil.when(() -> FileUtil.calculate_SHA1(any())).thenReturn("cf23df2207d99a74fbe169e3eba035e633b65d94");
            //doReturn("cf23df2207d99a74fbe169e3eba035e633b65d94").when(fUtil.when(() -> FileUtil.calculate_SHA1(any())));

            when(file.delete()).thenReturn(true);
            when(fileDAO.file_exists(any(), any())).thenReturn(true);
            returned = this.fileService.createNewFile(archionRequest, "save", "filename", file);
            fUtil.close();
        }


        assert(returned != null);
        assert (returned.getResponseObject().hasError());
        assert (returned.getResponseObject().getNotification().get(0).getName().contentEquals(ArchionConstants.FILE_ALREADY_EXISTS));
    }




    @Test
    public void correspondingFiletype_for_Test() throws Exception {
        ArchionRequest returned;
        this.setSampleRequest();

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            MockedStatic<FileUtil> fUtil = mockStatic(FileUtil.class);
            files.when(() -> Files.size(any())).thenReturn(Long.valueOf(10000));
            fUtil.when(() -> FileUtil.calculate_SHA1(any())).thenReturn("cf23df2207d99a74fbe169e3eba035e633b65d94");
            fUtil.when(() -> FileUtil.createFileInputStream(any())).thenReturn(Mockito.mock(FileInputStream.class));
            when(file.delete()).thenReturn(true);
            when(fileDAO.file_exists(any(), any())).thenReturn(true);
            Mockito.doReturn(this.mockSuccessfulstoreFile(archionRequest)).when(localMachineHandler).storeFile(any(), any());
            Mockito.doReturn(this.mockSuccessfulUploadFile(archionRequest)).when(cloudHandler).uploadFile(any(), any());

            Mockito.doReturn(archionRequest).when(fileDAO).create(any());
            Mockito.doReturn(archionRequest.getResponseObject().getFileMetadata()).when(fileDAO).update(any(), any());

            returned = this.fileService.createNewFile(archionRequest, "test", "filename", file);
            fUtil.close();
        }

        assert(returned != null);
        assert(returned.getResponseObject().getNotification().get(0).getType().equals(ArchionNotification.NotificationType.success));
        assert(returned.getResponseObject().getFileMetadata().getFiletype().equals(ArchionConstants.FILES_TEST_FILETYPE));

    }

    private FileMetadata mockSuccessfulstoreFile(ArchionRequest archionRequest){
        FileMetadata fileMetadata = archionRequest.getResponseObject().getFileMetadata();
        fileMetadata.setPhase(FileProcedurePhase.LOCAL_MACHINE_STORED);
        return fileMetadata;
    }

    private FileMetadata mockSuccessfulUploadFile(ArchionRequest archionRequest){
        FileMetadata fileMetadata = archionRequest.getResponseObject().getFileMetadata();
        fileMetadata.setPhase(FileProcedurePhase.CLOUD_SERVICE_STORED);
        return fileMetadata;
    }


    private void setSampleRequest(){
        ArchionRequest archionRequest = new ArchionRequest();

        ArchionUser archionUser = new ArchionUser();
        archionUser.setEmail("malandrakisgeo@gmail.com");
        archionUser.setId(UUID.randomUUID().toString());
        archionUser.setName("George Malandrakis");
        archionRequest.setUserObject(archionUser);

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFileid(UUID.randomUUID().toString());
        fileMetadata.setUserid(archionUser.getId());
        fileMetadata.setOriginalfilename("testfile");
        archionRequest.getResponseObject().setFileMetadata(fileMetadata);

        this.archionRequest = archionRequest;
    }

}
