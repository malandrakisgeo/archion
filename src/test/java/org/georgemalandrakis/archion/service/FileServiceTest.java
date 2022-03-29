package org.georgemalandrakis.archion.service;

import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.core.ArchionUser;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;

import java.util.UUID;

public class FileServiceTest {

    @Mock
    ConnectionManager connectionManager;

    @Mock
    CloudHandler cloudHandler;

    @Mock
    FileDAO fileDAO;

    @Mock
    LocalMachineHandler localMachineHandler;

    @InjectMocks
    FileService fileService;

    private ArchionRequest archionRequest;


    @BeforeTest
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(connectionManager.getLocalMachineFolder()).thenReturn("testMachine");
        this.setSampleRequest();

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
