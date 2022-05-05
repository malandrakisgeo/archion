package org.georgemalandrakis.archion.service;

import org.georgemalandrakis.archion.core.ArchionConstants;
import org.georgemalandrakis.archion.core.ArchionRequest;
import org.georgemalandrakis.archion.core.ArchionUser;
import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.dao.FileDAO;
import org.georgemalandrakis.archion.handlers.CloudHandler;
import org.georgemalandrakis.archion.handlers.LocalMachineHandler;
import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.other.FileUtil;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.UUID;



@RunWith(PowerMockRunner.class)
@PrepareForTest({FileService.class, Files.class, FileUtil.class})
public class PowerMockFileServiceTest {

    ConnectionManager connectionManager;

    CloudHandler cloudHandler;

    FileDAO fileDAO;

    File file;
    LocalMachineHandler localMachineHandler;


    private FileService fileService;


    private ArchionRequest archionRequest;

    @Before
    public void setUp() {
        fileService = new FileService(connectionManager, fileDAO, cloudHandler, localMachineHandler);
    }



    @Test
    public void correspondingFiletype_for_Test() throws Exception {
        ArchionRequest returned;
        FileInputStream fileInputStreamMock = PowerMockito.mock(FileInputStream.class);

        PowerMockito.whenNew(FileInputStream.class)
                .withAnyArguments().thenReturn(fileInputStreamMock);

         PowerMockito.mockStatic(Files.class);
        PowerMockito.mockStatic(FileUtil.class);

        //PowerMockito.when(Files.size(any())).thenReturn(Long.valueOf(10000));

        /*9
        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            MockedStatic<FileUtil> fUtil = mockStatic(FileUtil.class);
            files.when(() -> Files.size(any())).thenReturn(Long.valueOf(10000));
            fUtil.when(() -> FileUtil.calculate_SHA1(any())).thenReturn("cf23df2207d99a74fbe169e3eba035e633b65d94");
            //doReturn("cf23df2207d99a74fbe169e3eba035e633b65d94").when(fUtil.when(() -> FileUtil.calculate_SHA1(any())));

            when(file.delete()).thenReturn(true);
            when(fileDAO.file_exists(any(), any())).thenReturn(true);
            Mockito.doReturn(archionRequest).when(fileDAO).create(any());




        }
*/
        returned = this.fileService.createNewFile(archionRequest, "test", "filename", file);

        assert(returned != null);
        assert (returned.getResponseObject().hasError());
        assert (returned.getResponseObject().getNotification().get(0).getName().contentEquals(ArchionConstants.FILE_ALREADY_EXISTS));
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
