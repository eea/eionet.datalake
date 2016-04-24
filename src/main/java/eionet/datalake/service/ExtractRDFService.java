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
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
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
    public String generateRDFConfig(String datasetId, String baseURI, String vocabulary) throws IOException, SQLException {
        Connection connection = sqlService.getConnection(datasetId);
        //FIXME: load default properties first.
        Properties props = new Properties();
        ExploreDB dbExplorer = new ExploreDB(connection, props);
        dbExplorer.discoverTables(true);
        return extractProperties(props);
    }

    private String extractProperties(Properties props) {
        StringBuffer buf = new StringBuffer();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("sqldialect.")) {
                props.remove(key);
                continue;
            }
            buf.append(props.getProperty(key));
        }
        return buf.toString();
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
