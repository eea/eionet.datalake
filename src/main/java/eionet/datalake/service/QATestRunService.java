package eionet.datalake.service;

import eionet.datalake.dao.SQLService;
import eionet.datalake.dao.QATestService;
import eionet.datalake.dao.TestResultService;
import eionet.datalake.dao.EditionsService;
import eionet.datalake.model.QATest;
import eionet.datalake.model.QATestType;
import eionet.datalake.model.TestResult;
import eionet.datalake.model.Edition;
import eionet.datalake.util.BreadCrumbs;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for QA tests
 */

@Service
public class QATestRunService {

    @Autowired
    private EditionsService editionsService;

    @Autowired
    private SQLService sqlService;

    /**
     * Service for QA Test storage
     */
    @Autowired
    private QATestService qaTestService;

    @Autowired
    private TestResultService testResultService;


    /**
     * Run the QA tests on an edition.
     * 1. Get the QA tests.
     * 2. Run the tableExists tests and add the results to the database.
     * TODO: Update stats on edition.
     */
    public void runQAOnEdition(String editionId) throws IOException {
        Edition edition = editionsService.getById(editionId);
        try {
            String datasetId = edition.getFamilyId();
            List<QATest> qatests = qaTestService.getByFamilyId(datasetId);
            runTableExistsTests(qatests, edition);
            runSQLCheckTests(qatests, edition);
        } catch (SQLException e) {
           //
        }
    }

    /**
     * Run the table exists test by getting a list of tables from the edition.
     */
    private void runTableExistsTests(List<QATest> qatests, Edition edition) throws SQLException {
        List<String> tables = sqlService.metaTables(edition.getEditionId());
        HashMap<String, Boolean> pool = new HashMap<String, Boolean>();
        for (String table : tables) {
            pool.put(table.toUpperCase(), Boolean.valueOf(false));
        }
        TestResult result = new TestResult();
        String tableExists = QATestType.tableExists.name();
        for (QATest qatest : qatests) {
            if (!tableExists.equals(qatest.getTestType())) {
                continue;
            }
            result.setEditionId(edition.getEditionId());
            result.setTestId(qatest.getTestId());
            if (pool.containsKey(qatest.getQuery().trim().toUpperCase())) {
                result.setPassed(true);
                result.setResult("");
            } else {
                result.setPassed(false);
                result.setResult(qatest.getQuery().trim() + " not found");
            }
            testResultService.replace(result);
        }
    }

    /**
     * Run the SQL check tests.
     */
    private void runSQLCheckTests(List<QATest> qatests, Edition edition) {
        TestResult result = new TestResult();
        result.setEditionId(edition.getEditionId());
        String sqlCheck = QATestType.sqlCheck.name();
        for (QATest qatest : qatests) {
            if (!sqlCheck.equals(qatest.getTestType())) {
                continue;
            }
            result.setTestId(qatest.getTestId());
            try {
                List<List<String>> queryOutput = sqlService.executeSQLQuery(edition.getEditionId(), qatest.getQuery());
                if (queryOutput.size() != 1) {
                    result.setPassed(false);
                    result.setResult("More than one row returned");
                } else {
                    List<String> row = queryOutput.get(0);
                    String value = row.get(0).trim().toLowerCase();
                    String expected = qatest.getExpectedResult().trim().toLowerCase();
                    if (value != null && value.equals(expected)) {
                        result.setPassed(true);
                        result.setResult("");
                    } else {
                        result.setPassed(false);
                        result.setResult("Expected: " + expected + " - was: " + value);
                    }
                }
            } catch (Exception e) {
                result.setPassed(false);
                result.setResult(e.toString());
            }
            testResultService.replace(result);
        }
    }
}
