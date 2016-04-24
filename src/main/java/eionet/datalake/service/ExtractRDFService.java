package eionet.datalake.service;

import eionet.datalake.dao.DatasetService;
import eionet.datalake.dao.EditionsService;
import eionet.datalake.dao.SQLService;
import eionet.datalake.model.Dataset;
import eionet.datalake.model.Edition;
import eionet.datalake.util.Filenames;
import eionet.datalake.util.UniqueId;
import eionet.rdfexport.ExploreDB;
import eionet.rdfexport.GenerateRDF;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Service for extracting the MDB file as RDF including discovery.
 */

@Service
public class ExtractRDFService {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private EditionsService editionsService;

    @Autowired
    private SQLService sqlService;


    /**
     */
    public String generateRDFConfig(String datasetId) throws IOException, SQLException {
        Connection connection = sqlService.getConnection(datasetId);
        Properties props = new Properties();
        // Provide backup template from JAR file
        InputStream inStream = ExtractRDFService.class.getResourceAsStream("/explore.properties");
        try {
            props.load(inStream);
        } finally {
            inStream.close();
        }
        ExploreDB dbExplorer = new ExploreDB(connection, props);
        dbExplorer.discoverTables(true);
        return extractProperties(props);
    }

    /**
     * Convert the properties hashmap to a string.
     * Destroys the props content.
     */
    private String extractProperties(Properties props) throws IOException {
        //StringWriter w = new StringWriter();
        ByteArrayOutputStream w = new ByteArrayOutputStream();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("sqldialect.")) {
                props.remove(key);
                continue;
            }
        }
        props.store(w, "Automatically generated");
        return w.toString("ISO-8859-1");
    }

    /**
     * Generate RDF output and send it to the output stream.
     */
    public void generateRDF(OutputStream outputStream, String datasetId, String baseUri,
            String vocabularyUri, String script) throws IOException, SQLException {
        Properties props = new Properties();
        StringReader stringReader = new StringReader(script);
        props.load(stringReader);
        props.setProperty("baseurl", baseUri);
        props.setProperty("vocabulary", vocabularyUri);
        Connection connection = sqlService.getConnection(datasetId);
        GenerateRDF exporter = new GenerateRDF(outputStream, connection, props);
        String[] tablesToExport = exporter.getAllTables();
        for (String table : tablesToExport) {
            exporter.exportTable(table);
        }
        exporter.exportDocumentInformation();
        exporter.writeRdfFooter();
        connection.close();
    }
}
