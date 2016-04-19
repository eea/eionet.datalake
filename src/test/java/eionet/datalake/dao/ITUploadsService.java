package eionet.datalake.dao;
 
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.sql.Timestamp;
 
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

        String uuid1 = "pmuwddf2f6adsmu01ipzoa";
        createRecord(uuid1);
        String uuid2 = "okc1xpbpjquoq-nbeji0hq";
        createRecord(uuid2);

        //Read
        Upload doc1 = metadataService.getById(uuid1);
        assertNotNull(doc1);
        assertEquals(uuid1, doc1.getEditionId());
         
        //Get All
        List<Upload> docList = metadataService.getAll();
        assertEquals(2, docList.size());
    }
 
    private void createRecord(String uuid) throws Exception {
        Upload doc = new Upload();
        doc.setEditionId(uuid);
        doc.setFilename("testfile.txt");
        doc.setUploader("testperson");
        doc.setFamilyId("tukz0x0sdluyqce5l1c_hq");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        doc.setUploadTime(ts);
        metadataService.save(doc);
    }

}
