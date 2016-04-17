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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.Upload;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for datasets using JDBC.
 */
@Service
public class MetadataServiceJdbc implements MetadataService {

    //@Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Upload upload) {
        String query = "INSERT INTO datasets (id, filename, uploader, contenttype,"
                + " filesize, familyId, uploaddate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                upload.getId(), 
                upload.getFilename(),
                upload.getUploader(),
                upload.getContentType(),
                upload.getSize(),
                upload.getFamilyId(),
                upload.getUploadTime()
                );
    }

    @Override
    public Upload getById(String id) {
        String query = "SELECT id, filename, uploader, contenttype, filesize, familyId, uploaddate FROM datasets WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Upload uploadRec = jdbcTemplate.queryForObject(query, new Object[]{id}, new RowMapper<Upload>() {

            @Override
            public Upload mapRow(ResultSet rs, int rowNum) throws SQLException {
                Upload uploadRec = new Upload();
                uploadRec.setId(rs.getString("id"));
                uploadRec.setFilename(rs.getString("filename"));
                uploadRec.setUploader(rs.getString("uploader"));
                uploadRec.setContentType(rs.getString("contenttype"));
                uploadRec.setSize(rs.getLong("filesize"));
                uploadRec.setFamilyId(rs.getString("familyId"));
                uploadRec.setUploadTime(rs.getTimestamp("uploaddate"));
                return uploadRec;
            }
        });
        return uploadRec;
    }

    /**
     * Get all datasets, and only the attributes that are relevant.
     */
    @Override
    public List<Upload> getAll() {
        String query = "SELECT id, filename, uploader, contenttype, filesize FROM datasets";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Upload> uploadList = new ArrayList<Upload>();

        List<Map<String, Object>> uploadRows = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : uploadRows) {
            Upload uploadRec = new Upload();
            uploadRec.setId((String) (row.get("id")));
            uploadRec.setFilename((String) (row.get("filename")));
            uploadRec.setUploader((String) (row.get("uploader")));
            uploadRec.setContentType((String) (row.get("contenttype")));
            uploadRec.setSize((Long) (row.get("filesize")));
            uploadList.add(uploadRec);
        }
        return uploadList;
    }

    @Override
    public void deleteById(String id) {
        String query = "DELETE FROM datasets WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, id);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM datasets";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
