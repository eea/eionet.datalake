package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.QATest;
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
public class QATestServiceJdbc implements QATestService {

    @Autowired
    private DataSource dataSource;

/*
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
*/

    @Override
    public void save(QATest qatestRec) {
        String query = "INSERT INTO qatests (testid, datasetid, testtype, query, expectedresult)"
                + " VALUES (?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                qatestRec.getTestId(), 
                qatestRec.getDatasetId(),
                qatestRec.getTestType(),
                qatestRec.getQuery(),
                qatestRec.getExpectedResult()
                );
    }

    @Override
    public QATest getById(String testId) {
        String query = "SELECT testid, datasetid, testtype, query, expectedresult FROM qatests WHERE testid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        QATest qatestRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<QATest>(QATest.class), testId);
        return qatestRec;
    }

    /**
     * Get all qatests for a dataset.
     */
    @Override
    public List<QATest> getByDatasetId(String datasetId) {
        String query = "SELECT testid, datasetid, testtype, query, expectedresult FROM qatests WHERE datasetid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<QATest>(QATest.class), datasetId);
    }

    @Override
    public void deleteById(String testId) {
        String query = "DELETE FROM qatests WHERE testid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, testId);
    }

    @Override
    public void deleteTests(List<String> testIds) {
        for (String testId : testIds) {
            deleteById(testId);
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM qatests";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
