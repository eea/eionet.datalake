package eionet.datalake.controller;

import eionet.datalake.dao.DatasetService;
import eionet.datalake.dao.EditionsService;
import eionet.datalake.dao.QATestService;
import eionet.datalake.dao.SQLService;
import eionet.datalake.dao.TestResultService;
import eionet.datalake.model.Dataset;
import eionet.datalake.model.Edition;
import eionet.datalake.model.QATest;
import eionet.datalake.model.QATestType;
import eionet.datalake.model.TestResult;
import eionet.datalake.service.QATestRunService;
import eionet.datalake.util.BreadCrumbs;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping("/editions")
public class EditionController {

    @Autowired
    private DatasetService datasetService;

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

    @Autowired
    private QATestRunService qaTestRunService;

    /**
     * Actually show datasets - not editions.
     */
    @RequestMapping(value = "")
    public String listDatasets(Model model) {
        String pageTitle = "Dataset List";

        List<Dataset> datasets = datasetService.getAll();
        model.addAttribute("datasets", datasets);
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "datasets";
    }

    /**
     * Dataset query page.
     */
    @RequestMapping(value = "/{editionId}/query")
    public String editionQuery(
            @PathVariable("editionId") String fileId, final Model model) throws IOException, SQLException {
        Edition dataset = editionsService.getById(fileId);
        model.addAttribute("editionId", fileId);
        model.addAttribute("dataset", dataset);
        List<String> tables = sqlService.metaTables(fileId);
        model.addAttribute("tables", tables);
        return "editionQuery";
    }

    /**
     * Dataset query page.
     */
    @RequestMapping(value = "/{editionId}/query", method = RequestMethod.POST)
    public String editionQueryPost(
            @PathVariable("editionId") String fileId,
            @RequestParam("query") String query, final Model model) throws Exception {
        Edition dataset = editionsService.getById(fileId);
        model.addAttribute("editionId", fileId);
        model.addAttribute("query", query);
        model.addAttribute("dataset", dataset);

        List<List<String>> results = sqlService.executeSQLQuery(fileId, query);
        model.addAttribute("results", results);
        return "editionQuery";
    }


    /**
     * Dataset.
     */
    @RequestMapping(value = "/{editionId}")
    public String editionFactsheet(
            @PathVariable("editionId") String fileId, final Model model) throws Exception {
        model.addAttribute("editionId", fileId);
        Edition dataset = editionsService.getById(fileId);
        model.addAttribute("dataset", dataset);
        List<Edition> otherEditions = editionsService.getByDatasetId(dataset.getDatasetId());
        model.addAttribute("otherEditions", otherEditions);
        List<String> tables = sqlService.metaTables(fileId);
        model.addAttribute("tables", tables);
        List<TestResult> results = testResultService.getByEditionId(fileId);
        model.addAttribute("testresults", results);
        return "editionFactsheet";
    }

    /**
     * Create some QA tests for the dataset.
     */
    @RequestMapping(value = "/{editionId}/adddefaulttests")
    public String createQAtests(
            @PathVariable("editionId") String fileId, final Model model) throws Exception {
        Edition dataset = editionsService.getById(fileId);
        List<String> tables = sqlService.metaTables(fileId);
        QATest qatest = new QATest();
        String datasetId = dataset.getDatasetId();
        for (String table : tables) {
            qatest.setTestId(null);
            qatest.setDatasetId(datasetId);
            qatest.setTestType(QATestType.tableExists.name());
            qatest.setQuery(table);
            qatest.setExpectedResult("true");
            qaTestService.save(qatest);
        }
//      model.addAttribute("editionId", fileId);
//      model.addAttribute("dataset", dataset);
//      model.addAttribute("tables", tables);
        return "redirect:/qatests/" + datasetId;
    }

    /**
     * Run the QA tests on a dataset.
     */
    @RequestMapping(value = "/{editionId}/runqa")
    public String runQAOnEdition(@PathVariable("editionId") String fileId, final Model model) throws Exception {
        qaTestRunService.runQAOnEdition(fileId);
        return "redirect:/editions/" + fileId;
    }
}
