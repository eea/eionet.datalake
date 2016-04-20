package eionet.datalake.dao;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import eionet.datalake.model.Edition;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})

/**
 * Test the file operations.
 */
public class EditionsServiceIT {

    @Autowired
    private WebApplicationContext ctx;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void productionTest() throws Exception {
        EditionsService editionsService = ctx.getBean("editionsService", EditionsService.class);
        uploadAndDelete(editionsService);
    }

/*
    @Test
    public void swiftTest() throws Exception {
        EditionsService editionsService = ctx.getBean(EditionsServiceSwift.class);
        uploadAndDelete(editionsService);
    }

    @Test
    public void dbTest() throws Exception {
        EditionsService editionsService = ctx.getBean("editionsServiceDB", EditionsServiceDBFiles.class);
        uploadAndDelete(editionsService);
    }
*/

    private void uploadAndDelete(EditionsService editionsService) throws Exception {
        String testData = "ABCDEF";
        MultipartFile file = new MockMultipartFile("Testfile.txt", testData.getBytes());

        String newId = "datalake--remove--file";
        String familyId = "datalake-remove-family";
        editionsService.storeFile(file, newId, familyId);

        byte[] resultBuf = new byte[100];

        Edition upload = editionsService.getById(newId);
        InputStream infp = upload.getContentAsStream();
        infp.read(resultBuf);
        infp.close();
        assertEquals((byte) 0, resultBuf[6]);
        assertEquals(new String(resultBuf, 0, 6, Charset.forName("US-ASCII")), testData);
        assertTrue(editionsService.deleteById(newId));

        //exception.expect(IOException.class);
        // Can't delete twice.
        assertFalse(editionsService.deleteById(newId));
    }
}
