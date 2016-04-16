/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Transfer 1.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. All Rights Reserved.
 *
 * Contributor(s):
 *        Søren Roug
 */
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

    /** Active database connection. */
    private Connection connection;

    /**
     * The directory location where to store the uploaded files.
     */
    private String storageDir;

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    private void openConnection(String fileId) throws Exception {
        Class driverClass = Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        connection = DriverManager.getConnection("jdbc:ucanaccess://" + storageDir + "/" + fileId, "", "");
    }

    private void closeConnection() throws Exception {
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
    public List<String> metaTables(String fileId) throws Exception {
        return getTablesByType(fileId, "TABLE");
    }
    /**
     * Get views from database via metadata query.
     * @param args - unused.
     */
    public List<String> metaViews(String fileId) throws Exception {
        return getTablesByType(fileId, "VIEW");
    }

    private List<String> getTablesByType(String fileId, String tableType) throws Exception {
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

}
