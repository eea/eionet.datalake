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
import java.util.ArrayList;
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
        int countFailures = 0;
        List<QATest> qatests = new ArrayList<QATest>();
        try {
            String datasetId = edition.getDatasetId();
            qatests = qaTestService.getByDatasetId(datasetId);
            countFailures = runTableExistsTests(qatests, edition);
            countFailures += runSQLCheckTests(qatests, edition);
        } catch (SQLException e) {
           //
        }
        int countTests = qatests.size();
        editionsService.updateQAScore(editionId, countTests, countFailures);
    }

    /**
     * Run the table exists test by getting a list of tables from the edition.
     */
    private int runTableExistsTests(List<QATest> qatests, Edition edition) throws SQLException {
        int countFailures = 0;
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
                countFailures++;
                result.setResult(qatest.getQuery().trim() + " not found");
            }
            testResultService.replace(result);
        }
        return countFailures;
    }

    /**
     * Run the SQL check tests.
     */
    private int runSQLCheckTests(List<QATest> qatests, Edition edition) {
        int countFailures = 0;
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
                    countFailures++;
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
                        countFailures++;
                        result.setResult("Expected: " + expected + " - was: " + value);
                    }
                }
            } catch (Exception e) {
                result.setPassed(false);
                countFailures++;
                result.setResult(e.toString());
            }
            testResultService.replace(result);
        }
        return countFailures;
    }
}
