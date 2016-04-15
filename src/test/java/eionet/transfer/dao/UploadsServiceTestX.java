package eionet.datalake.dao;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import eionet.datalake.model.Upload;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UploadsServiceTestX {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void productionTest() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-db-config.xml");

        UploadsService uploadsService = ctx.getBean("uploadsService", UploadsService.class);
        uploadAndDelete(uploadsService);
        ctx.close();
    }

    @Test
    public void swiftTest() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-db-config.xml", "spring-uploadtest-config.xml");

        UploadsService uploadsService = ctx.getBean(UploadsServiceSwift.class);
        uploadAndDelete(uploadsService);
        ctx.close();
    }

    @Test
    public void dbTest() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-db-config.xml", "spring-uploadtest-config.xml");

        UploadsService uploadsService = ctx.getBean("uploadsServiceDB", UploadsServiceDBFiles.class);
        uploadAndDelete(uploadsService);
        ctx.close();
    }

    private void uploadAndDelete(UploadsService uploadsService) throws Exception {
        String testData = "ABCDEF";
        MultipartFile file = new MockMultipartFile("Testfile.txt", testData.getBytes());

        String newId = "fac61f06-8328-4490-846b-055bbc62fea6";
        uploadsService.storeFile(file, newId, 10);

        byte[] resultBuf = new byte[100];

        Upload upload = uploadsService.getById(newId);
        InputStream infp = upload.getContentAsStream();
        infp.read(resultBuf);
        infp.close();
        assertEquals((byte) 0, resultBuf[6]);
        assertEquals(new String(resultBuf, 0, 6, Charset.forName("US-ASCII")), testData);
        assertTrue(uploadsService.deleteById(newId));

        //exception.expect(IOException.class);
        // Can't delete twice.
        assertFalse(uploadsService.deleteById(newId));
    }
}
