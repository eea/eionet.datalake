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
import java.util.List;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Controller for dataset pages.
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
     * Run the QA tests on a dataset.
     * 1. Get the QA tests.
     * 2. Run the tableExists tests and add the results to the database.
     * TODO: Update stats on edition.
     */
    public void runQAOnEdition(String editionId) throws Exception {
        Edition dataset = editionsService.getById(editionId);
        String familyId = dataset.getFamilyId();
        List<QATest> qatests = qaTestService.getByFamilyId(familyId);
        runTableExistsTests(qatests, dataset);
        runSQLCheckTests(qatests, dataset);
    }

    /**
     * Run the table exists test by getting a list of tables from the dataset.
     */
    private void runTableExistsTests(List<QATest> qatests, Edition dataset) throws Exception {
        List<String> tables = sqlService.metaTables(dataset.getEditionId());
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
            result.setEditionId(dataset.getEditionId());
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
    private void runSQLCheckTests(List<QATest> qatests, Edition dataset) throws Exception {
        TestResult result = new TestResult();
        result.setEditionId(dataset.getEditionId());
        String sqlCheck = QATestType.sqlCheck.name();
        for (QATest qatest : qatests) {
            if (!sqlCheck.equals(qatest.getTestType())) {
                continue;
            }
            result.setTestId(qatest.getTestId());
            List<List<String>> queryOutput = sqlService.executeSQLQuery(dataset.getEditionId(), qatest.getQuery());
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
            testResultService.replace(result);
        }
    }
}
