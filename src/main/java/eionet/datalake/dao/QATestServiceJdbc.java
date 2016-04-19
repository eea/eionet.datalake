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

/**
 * Service to store metadata for datasets using JDBC.
 */
@Service
public class QATestServiceJdbc implements QATestService {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(QATest qatestRec) {
        String query = "INSERT INTO qatests (testid, familyid, testtype, query, expectedresult)"
                + " VALUES (?, ?, ?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                qatestRec.getTestId(), 
                qatestRec.getFamilyId(),
                qatestRec.getTestType(),
                qatestRec.getQuery(),
                qatestRec.getExpectedResult()
                );
    }

    @Override
    public QATest getById(String testId) {
        String query = "SELECT testid, familyid, testtype, query, expectedresult FROM qatests WHERE testid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        QATest qatestRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<QATest>(QATest.class), testId);
        return qatestRec;
    }

    /**
     * Get all qatests for a family.
     */
    @Override
    public List<QATest> getByFamilyId(String familyId) {
        String query = "SELECT testid, familyid, testtype, query, expectedresult FROM qatests WHERE familyid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<QATest>(QATest.class), familyId);
    }

    @Override
    public void deleteById(String testId) {
        String query = "DELETE FROM qatests WHERE testid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, testId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM qatests";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }
}
