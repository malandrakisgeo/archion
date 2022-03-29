package org.georgemalandrakis.archion.dao;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

public class FileDAOTest {

    @Mock
    ConnectionManager connectionManager;

    @InjectMocks
    FileDAO testObj;

    @BeforeMethod
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


}
