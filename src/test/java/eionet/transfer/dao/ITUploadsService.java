package eionet.datalake.dao;
 
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
 
import org.springframework.context.support.ClassPathXmlApplicationContext;
 
import eionet.datalake.model.Upload;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ITUploadsService {
 
    private MetadataService metadataService;

    private ClassPathXmlApplicationContext ctx;

    @Before
    public void loadContext() {
        //Get the Spring Context
        ctx = new ClassPathXmlApplicationContext("spring-db-config.xml");
         
        //Get the MetadataService Bean from the context.
        metadataService = ctx.getBean("metadataService", MetadataService.class);

        //Start from an empty database.
        metadataService.deleteAll();
    }

    @After
    public void closeContext() {
        //Close Spring Context
        ctx.close();
    }

    @Test
    public void simpleTest() throws Exception {

        String uuid1 = "b1dd4c8e-18b4-445c-bc75-d373dad22c40";
        createRecord(uuid1, "2013-05-01 19:00:01");
        String uuid2 = "62c8a681-bf6f-4d88-878c-a2e92ea310e1";
        createRecord(uuid2, "2015-01-01 09:00:01");

        //Read
        Upload doc1 = metadataService.getById(uuid1);
        assertNotNull(doc1);
        assertEquals(uuid1, doc1.getId());
         
        //Get All
        List<Upload> docList = metadataService.getAll();
        assertEquals(2, docList.size());

        //Get expired as of now.
        List<String> expiredList = metadataService.getExpired();
        assertEquals(2, expiredList.size());

        // Get expired as of Jan 2014.
        Date expirationDate3 = new Date(stringDatetimeToTimestamp("2014-01-01 09:00:01"));
        List<String> expiredList2 = metadataService.getExpired(expirationDate3);
        assertEquals(1, expiredList2.size());

        String expiredId1 = expiredList.get(0);
        assertEquals(uuid1, expiredId1);
    }
 
    private void createRecord(String uuid, String expiration) throws Exception {
        Upload doc = new Upload();
        doc.setId(uuid);
        doc.setFilename("testfile.txt");
        Date expirationDate = new Date(stringDatetimeToTimestamp(expiration));
        doc.setExpires(expirationDate);
        doc.setUploader("testperson");
        metadataService.save(doc);
    }

    /**
     * Mock function that converts a datetime value in string format into a timestamp
     * value, in the same way that the MySql jdbc driver does.
     * 
     * @param datetime The datetime value in yyyy-MM-dd HH:mm:ss format
     * @return The corresponding unix timestamp
     * @throws ParseException 
     */
    private long stringDatetimeToTimestamp(String datetime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = format.parse(datetime).getTime();

        return millis;
    }

}
