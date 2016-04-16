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
 
import eionet.datalake.dao.StorageServiceFiles;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StorageServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void simpleTest() throws Exception {
        //Get the Spring Context
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-db-config.xml");
         
        //Get the StorageService Bean from the context.
        StorageService storageService = ctx.getBean("storageService", StorageService.class);

        String testData = "ABCDEF";
        MultipartFile file = new MockMultipartFile("Testfile.txt", testData.getBytes());

        String newId = "fac61f06-8328-4490-846b-055bbc62fea6";
        storageService.save(file, newId);
        //assertNotNull(newId);

        byte[] resultBuf = new byte[100];

        InputStream infp = storageService.getById(newId);
        infp.read(resultBuf);
        infp.close();
        assertEquals((byte) 0, resultBuf[6]);
        assertEquals(new String(resultBuf, 0, 6, Charset.forName("US-ASCII")), testData);
        assertTrue(storageService.deleteById(newId));

        //exception.expect(IOException.class);
        assertFalse(storageService.deleteById(newId));
         
        ctx.close(); //Close Spring Context
    }
}
