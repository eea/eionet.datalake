package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.TestResult;
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
public class TestResultServiceJdbc implements TestResultService {

    @Autowired
    private DataSource dataSource;

/*
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
*/

    @Override
    public void replace(TestResult testResultRec) {
        deleteOneById(testResultRec.getTestId(), testResultRec.getEditionId());
        save(testResultRec);
    }

    @Override
    public void save(TestResult testResultRec) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        testResultRec.setTestRan(ts);
        String query = "INSERT INTO testresults (testid, editionid, passed, testran, result)"
                + " VALUES (?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                testResultRec.getTestId(), 
                testResultRec.getEditionId(), 
                testResultRec.getPassed(),
                testResultRec.getTestRan(),
                testResultRec.getResult()
                );
    }

    @Override
    public TestResult getOneById(Integer testId, String editionId) {
        String query = "SELECT testid, editionid, passed, testran, result FROM testresults WHERE testid = ? AND editionid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        TestResult testResultRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<TestResult>(TestResult.class), testId, editionId);
        return testResultRec;
    }

    /**
     * Get all testresults for an edition.
     */
    @Override
    public List<TestResult> getByEditionId(String editionId) {
        String query = "SELECT testid, editionid, passed, testran, result FROM testresults WHERE editionid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<TestResult>(TestResult.class), editionId);
    }

    @Override
    public void deleteOneById(Integer testId, String editionId) {
        String query = "DELETE FROM testresults WHERE testid = ? AND editionid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, testId, editionId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM testresults";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
