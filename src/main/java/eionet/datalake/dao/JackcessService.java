package eionet.datalake.dao;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.query.Query;
import com.healthmarketscience.jackcess.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service to store files in the file system.
 */
@Service
public class JackcessService {

    /** Active database db. */
    private Database db;

    /**
     * The directory location where to store the uploaded files.
     */
    private String storageDir;

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public Database getDatabase(String fileId) throws IOException {
        openDatabase(fileId);
        return db;
    }

    private void openDatabase(String fileId) throws IOException {
        db = new DatabaseBuilder(new File(createDatabaseString(fileId))).setReadOnly(true).open();
    }

    private String createDatabaseString(String fileId) {
        return storageDir + "/" + fileId;
    }

    public void closeDatabase() throws IOException {
        db.close();
        db = null;
    }

    /**
     * Get tables from database via metadata query.
     * @param args - unused.
     */
    public List<String> metaTables(String fileId) throws IOException {
        openDatabase(fileId);
        Set<String> tables = db.getTableNames();
        ArrayList<String> tableNamesList = new ArrayList<String>();
        for (String t : tables) {
            tableNamesList.add(t);
        }
        closeDatabase();
        return tableNamesList;
    }
    /**
     * Get views from database via metadata query.
     * @param fileId - name of MS-Access file.
     */
    public List<String> metaViews(String fileId) throws IOException {
        openDatabase(fileId);
        List<Query> queries = db.getQueries();
        ArrayList<String> queryNamesList = new ArrayList<String>();
        for (Query q : queries) {
            queryNamesList.add(q.getName());
        }
        closeDatabase();
        return queryNamesList;
    }

    public Map<String, List<String>> getColumns(String fileId) throws IOException {
        HashMap<String, List<String>> tableColumns = new HashMap<String, List<String>>();
        openDatabase(fileId);
        Set<String> tables = db.getTableNames();
        for (String t : tables) {
            Table tableObj = db.getTable(t);
            List<? extends Column> columnObjs = tableObj.getColumns();
            ArrayList<String> columnList = new ArrayList<String>();
            for (Column c : columnObjs) {
                columnList.add(c.getName());
            }
            tableColumns.put(t, columnList);
        }
        closeDatabase();
        return tableColumns;
    }

    private List<String> getIndexByTable(String fileId, String table) throws IOException {
        openDatabase(fileId);
        Table tableObj = db.getTable(table);
        List<? extends Index> indexes = tableObj.getIndexes();
        ArrayList<String> indexNamesList = new ArrayList<String>();
        for (Index i : indexes) {
            indexNamesList.add(i.getName());
        }
        closeDatabase();
        return indexNamesList;
    }
}
