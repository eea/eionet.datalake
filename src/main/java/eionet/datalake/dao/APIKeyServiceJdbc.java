package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.APIKey;
import eionet.datalake.model.TestResult;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for API keys using JDBC.
 */
@Service
public class APIKeyServiceJdbc implements APIKeyService {

    @Autowired
    private DataSource dataSource;

    @Override
    public void insert(APIKey record) {
        String query = "INSERT INTO apikeys (identifier, scope, key_value) VALUES (?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                record.getIdentifier(),
                record.getScope(),
                record.getKeyValue()
                );
    }

    @Override
    public APIKey getById(String identifier) {
        String query = "SELECT identifier, scope, key_value FROM apikeys"
                + " WHERE identifier = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        APIKey record = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<APIKey>(APIKey.class), identifier);
        return record;
    }

    @Override
    public APIKey getByKeyValue(String keyValue) {
        String query = "SELECT identifier, scope, key_value FROM apikeys"
                + " WHERE key_value = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        APIKey record = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<APIKey>(APIKey.class), keyValue);
        return record;
    }

    /**
     * Get all editions, and only the attributes that are relevant.
     */
    @Override
    public List<APIKey> getAll() {
        String query = "SELECT identifier, scope, key_value FROM apikeys";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<APIKey>(APIKey.class));
    }

    @Override
    public void deleteById(String identifier) {
        String query = "DELETE FROM apikeys WHERE identifier = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, identifier);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM apikeys";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }

    @Override
    public void update(APIKey record) {
        String update = "UPDATE apikeys SET scope = ?, key_value = ? WHERE identifier = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(update,
                record.getScope(),
                record.getKeyValue(),
                record.getIdentifier());
    }

}
