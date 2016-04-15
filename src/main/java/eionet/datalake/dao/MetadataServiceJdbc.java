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

import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * Service to store metadata for uploaded files using JDBC.
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
        String query = "INSERT INTO uploads (id, expires, filename, uploader, contenttype, filesize) VALUES (?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, upload.getId(), upload.getExpires(),
            upload.getFilename(), upload.getUploader(),
            upload.getContentType(), upload.getSize());
    }

    @Override
    public Upload getById(String id) {
        String query = "SELECT id, expires, filename, uploader, contenttype, filesize FROM uploads WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Upload uploadRec = jdbcTemplate.queryForObject(query, new Object[]{id}, new RowMapper<Upload>() {

            @Override
            public Upload mapRow(ResultSet rs, int rowNum) throws SQLException {
                Upload uploadRec = new Upload();
                uploadRec.setId(rs.getString("id"));
                uploadRec.setFilename(rs.getString("filename"));
                uploadRec.setExpires(rs.getDate("expires"));
                uploadRec.setUploader(rs.getString("uploader"));
                uploadRec.setContentType(rs.getString("contenttype"));
                uploadRec.setSize(rs.getLong("filesize"));
                return uploadRec;
            }
        });

        return uploadRec;
    }

    @Override
    public List<Upload> getAll() {
        String query = "SELECT id, expires, filename, uploader, contenttype, filesize FROM uploads";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Upload> uploadList = new ArrayList<Upload>();

        List<Map<String, Object>> uploadRows = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : uploadRows) {
            Upload uploadRec = new Upload();
            uploadRec.setId((String) (row.get("id")));
            uploadRec.setFilename((String) (row.get("filename")));
            uploadRec.setExpires((Date) (row.get("expires")));
            uploadRec.setUploader((String) (row.get("uploader")));
            uploadRec.setContentType((String) (row.get("contenttype")));
            uploadRec.setSize((Long) (row.get("filesize")));
            uploadList.add(uploadRec);
        }
        return uploadList;
    }

    @Override
    public void deleteById(String id) {
        String query = "DELETE FROM uploads WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<String> getExpired() {
        Date now = new Date(System.currentTimeMillis());
        return getExpired(now);
    }

    @Override
    public List<String> getExpired(Date expireDate) {
        String query = "SELECT id FROM uploads WHERE expires < ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> uploadList = new ArrayList<String>();

        Object[] parameters = new Object[] {expireDate};
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, parameters);
        for (Map<String, Object> row : rows) {
            String uuidName = new String((String) row.get("id"));
            uploadList.add(uuidName);
        }
        return uploadList;
    }

    @Override
    public List<Upload> getUnexpired() {
        Date now = new Date(System.currentTimeMillis());
        return getUnexpired(now);
    }

    @Override
    public List<Upload> getUnexpired(Date expireDate) {
        List<Upload> allUploads = getAll();
        List<Upload> uploadList = new ArrayList<Upload>();

        for (Upload upload : allUploads) {
            if (expireDate.before(upload.getExpires())) {
                uploadList.add(upload);
            }
        }
        return uploadList;
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM uploads";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
