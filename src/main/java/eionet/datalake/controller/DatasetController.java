package eionet.datalake.controller;

import eionet.datalake.dao.SQLService;
import eionet.datalake.dao.QATestService;
import eionet.datalake.dao.TestResultService;
import eionet.datalake.dao.UploadsService;
import eionet.datalake.model.QATest;
import eionet.datalake.model.QATestType;
import eionet.datalake.model.TestResult;
import eionet.datalake.model.Upload;
import eionet.datalake.util.BreadCrumbs;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for dataset pages.
 */

@Controller
@RequestMapping("/datasets")
public class DatasetController {

    @Autowired
    private UploadsService uploadsService;

    @Autowired
    private SQLService sqlService;

    /**
     * Service for QA Test storage
     */
    @Autowired
    QATestService qaTestService;

    @Autowired
    private TestResultService testResultService;

    @RequestMapping(value = "")
    public String listDatasets(Model model) {
        String pageTitle = "Dataset List";

        List<Upload> editions = uploadsService.getAll();
        model.addAttribute("editions", editions);
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "datasets";
    }

    /**
     * Dataset query page.
     */
    @RequestMapping(value = "/{uuid}/query")
    public String datasetQuery(
            @PathVariable("uuid") String fileId, final Model model) throws IOException {
        Upload dataset = uploadsService.getById(fileId);
        model.addAttribute("uuid", fileId);
        model.addAttribute("dataset", dataset);
        return "datasetQuery";
    }

    /**
     * Dataset query page.
     */
    @RequestMapping(value = "/{uuid}/query", method = RequestMethod.POST)
    public String datasetQueryPost(
            @PathVariable("uuid") String fileId,
            @RequestParam("query") String query, final Model model) throws Exception {
        Upload dataset = uploadsService.getById(fileId);
        model.addAttribute("uuid", fileId);
        model.addAttribute("query", query);
        model.addAttribute("dataset", dataset);

        List<List<String>> results = sqlService.executeSQLQuery(fileId, query);
        model.addAttribute("results", results);
        return "datasetQuery";
    }


    /**
     * Dataset.
     */
    @RequestMapping(value = "/{uuid}")
    public String datasetFactsheet(
            @PathVariable("uuid") String fileId, final Model model) throws Exception {
        model.addAttribute("uuid", fileId);
        Upload dataset = uploadsService.getById(fileId);
        model.addAttribute("dataset", dataset);
        List<Upload> otherEditions = uploadsService.getByFamilyId(dataset.getFamilyId());
        model.addAttribute("otherEditions", otherEditions);
        List<String> tables = sqlService.metaTables(fileId);
        model.addAttribute("tables", tables);
        List<TestResult> results = testResultService.getByEditionId(fileId);
        model.addAttribute("testresults", results);
        int successes = 0;
        int failures = 0;
        int numTests = 0;
        for (TestResult result : results) {
            numTests++;
            if (result.getPassed() == Boolean.TRUE) successes++; else failures++;
        }
        model.addAttribute("successes", successes);
        model.addAttribute("failures", failures);
        model.addAttribute("numTests", numTests);
        return "datasetFactsheet";
    }

    /**
     * Create some QA tests for the dataset.
     */
    @RequestMapping(value = "/{uuid}/adddefaulttests")
    public String createQAtests(
            @PathVariable("uuid") String fileId, final Model model) throws Exception {
        Upload dataset = uploadsService.getById(fileId);
        List<String> tables = sqlService.metaTables(fileId);
        QATest qatest = new QATest();
        String familyId = dataset.getFamilyId();
        for (String table : tables) {
            qatest.setTestId(null);
            qatest.setFamilyId(familyId);
            qatest.setTestType(QATestType.tableExists.name());
            qatest.setQuery(table);
            qatest.setExpectedResult("true");
            qaTestService.save(qatest);
        }
//      model.addAttribute("uuid", fileId);
//      model.addAttribute("dataset", dataset);
//      model.addAttribute("tables", tables);
        return "redirect:/qatests/" + familyId;
    }

    /**
     * Run the QA tests on a dataset.
     * 1. Get the QA tests.
     * 2. Run the tableExists tests and add the results to the database.
     * TODO
     */
    @RequestMapping(value = "/{uuid}/runqa")
    public String runQAOnEdition(@PathVariable("uuid") String fileId, final Model model) throws Exception {
        Upload dataset = uploadsService.getById(fileId);
        String familyId = dataset.getFamilyId();
        List<QATest> qatests = qaTestService.getByFamilyId(familyId);
        runTableExistsTests(qatests, dataset);
        return "redirect:/datasets/" + fileId;
    }

    /**
     * Run the table exists test by getting a list of tables from the dataset.
     */
    private void runTableExistsTests(List<QATest> qatests, Upload dataset) throws Exception {
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
}
