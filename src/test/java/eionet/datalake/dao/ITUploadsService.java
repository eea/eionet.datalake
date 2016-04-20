package eionet.datalake.dao;
 
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eionet.datalake.model.Edition;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml", "classpath:spring-db-config.xml"})
public class ITUploadsService {
 
    @Autowired
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
        Edition doc1 = metadataService.getById(uuid1);
        assertNotNull(doc1);
        assertEquals(uuid1, doc1.getEditionId());
         
        //Get All
        List<Edition> docList = metadataService.getAll();
        assertEquals(2, docList.size());
    }
 
    private void createRecord(String uuid) throws Exception {
        Edition doc = new Edition();
        doc.setEditionId(uuid);
        doc.setFilename("testfile.txt");
        doc.setUploader("testperson");
        doc.setFamilyId("tukz0x0sdluyqce5l1c_hq");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        doc.setUploadTime(ts);
        metadataService.save(doc);
    }

}
