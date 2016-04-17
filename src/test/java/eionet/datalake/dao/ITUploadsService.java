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

        String uuid1 = "b1dd4c8e-18b4-445c-bc75-d373dad22c40";
        createRecord(uuid1);
        String uuid2 = "62c8a681-bf6f-4d88-878c-a2e92ea310e1";
        createRecord(uuid2);

        //Read
        Upload doc1 = metadataService.getById(uuid1);
        assertNotNull(doc1);
        assertEquals(uuid1, doc1.getId());
         
        //Get All
        List<Upload> docList = metadataService.getAll();
        assertEquals(2, docList.size());
    }
 
    private void createRecord(String uuid) throws Exception {
        Upload doc = new Upload();
        doc.setId(uuid);
        doc.setFilename("testfile.txt");
        doc.setUploader("testperson");
        doc.setFamilyId("17ec5cef-677c-4808-a3fe-3bef6b8195a0");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        doc.setUploadTime(ts);
        metadataService.save(doc);
    }

}
