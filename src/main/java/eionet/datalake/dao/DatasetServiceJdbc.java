package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.Dataset;
import eionet.datalake.model.TestResult;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for datasets using JDBC.
 */
@Service
public class DatasetServiceJdbc implements DatasetService {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(Dataset datasetRec) {
        String query = "INSERT INTO families (familyid, title, keep) VALUES (?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                datasetRec.getFamilyId(),
                datasetRec.getTitle(),
                datasetRec.getKeep()
                );
    }

    @Override
    public Dataset getById(String familyId) {
        String query = "SELECT familyid, title, keep FROM families WHERE familyid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Dataset datasetRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<Dataset>(Dataset.class), familyId);
        return datasetRec;
    }

    @Override
    public void deleteById(String familyId) {
        String query = "DELETE FROM families WHERE familyid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, familyId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM families";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
