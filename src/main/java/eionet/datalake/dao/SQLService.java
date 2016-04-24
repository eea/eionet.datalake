package eionet.datalake.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service to store files in the file system.
 */
@Service
public class SQLService {

    public static final String MDB_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String PREFIX = "jdbc:ucanaccess://";
    private static final String MODIFIERS = ";memory=false";

    /** Active database connection. */
    private Connection connection;

    /**
     * The directory location where to store the uploaded files.
     */
    private String storageDir;

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public Connection getConnection(String fileId) throws SQLException {
        openConnection(fileId);
        return connection;
    }

    private void openConnection(String fileId) throws SQLException {
        //Class driverClass = Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        connection = DriverManager.getConnection(createConnectionString(fileId));
    }

    public String createConnectionString(String fileId) {
        return PREFIX + storageDir + "/" + fileId + MODIFIERS;
    }

    public void closeConnection() throws SQLException {
        connection.close();
        connection = null;
    }

    /**
     * Send query to database.
     *
     * @param query - the input text from the user.
     */
    public List<List<String>> executeSQLQuery(String fileId, String query) throws Exception {
        openConnection(fileId);
        Statement st = connection.createStatement();
        ResultSet rs = null;
        List<List<String>> resultList = new ArrayList<List<String>>();
        int currentRow = 0;
        //FIXME: show number of records affected on updates. Check if there is more than one result set.
        try {
            if (st.execute(query)) {
                rs = st.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int numcols = rsmd.getColumnCount();
                while (rs.next()) {
                    currentRow += 1;
                    ArrayList<String> rowList = new ArrayList<String>();
                    resultList.add(rowList);
                    for (int i = 1; i <= numcols; i++) {
                        rowList.add(rs.getString(i));
                    }
                    if (currentRow >= 300) {
                        break;
                    }
                }
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            closeConnection();
        }
        return resultList;
    }

    /**
     * Get tables from database via metadata query.
     * @param args - unused.
     */
    public List<String> metaTables(String fileId) throws SQLException {
        return getTablesByType(fileId, "TABLE");
    }
    /**
     * Get views from database via metadata query.
     * @param args - unused.
     */
    public List<String> metaViews(String fileId) throws SQLException {
        return getTablesByType(fileId, "VIEW");
    }

    private List<String> getTablesByType(String fileId, String tableType) throws SQLException {
        String catalogPattern = null;
        String schemaPattern = null;

        openConnection(fileId);
        DatabaseMetaData dbMetadata = connection.getMetaData();

        ResultSet rs = null;
        rs = dbMetadata.getTables(catalogPattern, schemaPattern, "%", new String[] {tableType});
        ArrayList<String> tableList = new ArrayList<String>();
        
        while (rs.next()) {
            tableList.add(rs.getString(3));
        }
        rs.close();
        closeConnection();
        return tableList;
    }

    private List<String> getIndexByTable(String fileId, String table) throws SQLException {
        String catalogPattern = null;
        String schemaPattern = null;

        openConnection(fileId);
        DatabaseMetaData dbMetadata = connection.getMetaData();

        ResultSet rs = dbMetadata.getIndexInfo(catalogPattern, schemaPattern, table, false, false);
        ArrayList<String> indexList = new ArrayList<String>();
        
        while (rs.next()) {
            if (rs.getString(6) != null) {
                indexList.add(rs.getString(6));
            }
        }
        rs.close();
        closeConnection();
        return indexList;
    }
}
