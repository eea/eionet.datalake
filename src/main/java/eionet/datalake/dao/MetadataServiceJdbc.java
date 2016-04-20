package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.Upload;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for editions using JDBC.
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
        String query = "INSERT INTO editions (editionid, filename, uploader, contenttype,"
                + " filesize, familyId, uploadtime) VALUES (?, ?, ?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                upload.getEditionId(), 
                upload.getFilename(),
                upload.getUploader(),
                upload.getContentType(),
                upload.getFileSize(),
                upload.getFamilyId(),
                upload.getUploadTime()
                );
    }

    @Override
    public Upload getById(String editionId) {
        String query = "SELECT editionid, filename, uploader, contenttype, filesize, familyid, uploadtime FROM editions WHERE editionid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<Upload>(Upload.class), editionId);
/*
        Upload uploadRec = jdbcTemplate.queryForObject(query, new Object[]{editionId}, new RowMapper<Upload>() {

            @Override
            public Upload mapRow(ResultSet rs, int rowNum) throws SQLException {
                Upload uploadRec = new Upload();
                uploadRec.setEditionId(rs.getString("editionid"));
                uploadRec.setFilename(rs.getString("filename"));
                uploadRec.setUploader(rs.getString("uploader"));
                uploadRec.setContentType(rs.getString("contenttype"));
                uploadRec.setFileSize(rs.getLong("filesize"));
                uploadRec.setFamilyId(rs.getString("familyId"));
                uploadRec.setUploadTime(rs.getTimestamp("uploadtime"));
                return uploadRec;
            }
        });
        return uploadRec;
*/
    }

    @Override
    public List<Upload> getByFamilyId(String familyId) {
        String query = "SELECT editionid, filename, uploader, contenttype, filesize, familyid, uploadtime FROM editions WHERE familyid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<Upload>(Upload.class), familyId);
    }

    /**
     * Get all editions, and only the attributes that are relevant.
     */
    @Override
    public List<Upload> getAll() {
        String query = "SELECT editionid, filename, uploader, contenttype, filesize FROM editions";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<Upload>(Upload.class));
    }

    @Override
    public void deleteById(String editionId) {
        String query = "DELETE FROM editions WHERE editionid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, editionId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM editions";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
