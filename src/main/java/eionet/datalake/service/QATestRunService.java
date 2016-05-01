package eionet.datalake.service;

import eionet.datalake.dao.SQLService;
import eionet.datalake.dao.JackcessService;
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
    private JackcessService jackcessService;

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
        } catch (IOException e) {
           //
        }
        int countTests = qatests.size();
        editionsService.updateQAScore(editionId, countTests, countFailures);
    }

    /**
     * Run the table exists test by getting a list of tables from the edition.
     */
    private int runTableExistsTests(List<QATest> qatests, Edition edition) throws IOException {
        int countFailures = 0;
        List<String> tables = jackcessService.metaTables(edition.getEditionId());
        HashMap<String, Boolean> pool = new HashMap<String, Boolean>();
        for (String table : tables) {
            pool.put(table.toUpperCase(), Boolean.valueOf(false));
        }
        String tableExists = QATestType.tableExists.name();
        for (QATest qatest : qatests) {
            if (!tableExists.equals(qatest.getTestType())) {
                continue;
            }
            TestResult result = runOneTableExistsTest(qatest, pool);
            if (result.getPassed() == false) {
                countFailures++;
            }
            result.setTestId(qatest.getTestId());
            result.setEditionId(edition.getEditionId());
            testResultService.replace(result);
        }
        return countFailures;
    }

    /**
     * Run one table exist test.
     */
    private TestResult runOneTableExistsTest(QATest qatest, HashMap<String, Boolean> pool) throws IOException {
        TestResult result = new TestResult();
        result.setPassed(true);
        result.setResult("");
        boolean expectedBool = getExpectedAsBool(qatest);
        String query = qatest.getQuery().trim().toUpperCase();
        String[] tables = query.split("\\s*,\\s*");
        List<String> failures = new ArrayList<String>(tables.length);

        for (String table : tables) {
            if ("".equals(table)) {
                continue;
            }
            if (expectedBool) {
                if (!pool.containsKey(table)) {
                    failures.add(table);
                }
            } else {
                if (pool.containsKey(table)) {
                    failures.add(table);
                }
            }
        }
        if (failures.size() > 0) {
            if (expectedBool) {
                result.setResult("Not found: " + String.join(", ", failures));
            } else {
                result.setResult("Not expected: " + String.join(", ", failures));
            }
            result.setPassed(false);
        }
        return result;
    }

    private boolean getExpectedAsBool(QATest qatest) {
        String expected = qatest.getExpectedResult().trim().toLowerCase();
        boolean expectedBool = false;
        if ("true".equals(expected) || "1".equals(expected) || "yes".equals(expected) || "".equals(expected)) {
            expectedBool = true;
        }
        return expectedBool;
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
