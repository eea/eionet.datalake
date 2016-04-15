//package eionet.transfer.dao;
 
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
 
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
 
import eionet.transfer.model.Upload;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ITShowDataSource {
 
    private DriverManagerDataSource dataSource;

    private ClassPathXmlApplicationContext ctx;

    @Before
    public void loadContext() {
        //Get the Spring Context
        ctx = new ClassPathXmlApplicationContext("spring-db-config.xml");
         
        dataSource = ctx.getBean("dataSource", DriverManagerDataSource.class);
    }

    @After
    public void closeContext() {
        ctx.close();
    }

    @Test
    public void simpleTest() throws Exception {
        System.out.println("DATASOURCE=" + dataSource.getUrl());
    }

}
