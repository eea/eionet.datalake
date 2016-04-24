package eionet.datalake.controller;

import eionet.datalake.dao.DatasetService;
import eionet.datalake.dao.EditionsService;
import eionet.datalake.dao.QATestService;
import eionet.datalake.dao.SQLService;
import eionet.datalake.model.Dataset;
import eionet.datalake.model.Edition;
import eionet.datalake.model.QATest;
import eionet.datalake.model.QATestType;
import eionet.datalake.service.QATestRunService;
import eionet.datalake.service.ExtractRDFService;
import eionet.datalake.util.BreadCrumbs;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private ExtractRDFService extractRDFService;

    @Autowired
    private QATestRunService qaTestRunService;

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
     * Dataset.
     */
    @RequestMapping(value = {"/{datasetId}", "/{datasetId}/view"})
    public String datasetFactsheet(
            @PathVariable("datasetId") String fileId, final Model model) throws Exception {
        model.addAttribute("datasetId", fileId);
        Dataset dataset = datasetService.getById(fileId);
        model.addAttribute("dataset", dataset);
        List<Edition> otherEditions = editionsService.getByDatasetId(dataset.getDatasetId());
        model.addAttribute("otherEditions", otherEditions);
        return "datasetFactsheet";
    }

    /**
     * Create some QA tests for the dataset.
     */
    @RequestMapping(value = "/{datasetId}/adddefaulttests")
    public String createQAtests(
            @PathVariable("datasetId") String fileId, final Model model) throws Exception {
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
//      model.addAttribute("datasetId", fileId);
//      model.addAttribute("dataset", dataset);
//      model.addAttribute("tables", tables);
        return "redirect:/qatests/" + datasetId;
    }

    /**
     * Run the QA tests on a dataset.
     */
    @RequestMapping(value = "/{datasetId}/runqa")
    public String runQAOnEdition(@PathVariable("datasetId") String fileId, final Model model) throws Exception {
        qaTestRunService.runQAOnEdition(fileId);
        return "redirect:/datasets/" + fileId;
    }

    /**
     * Form for editing existing QA test.
     *
     * @param testid
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping("/{datasetid}/edit")
    public String editDataset(Model model,
            @PathVariable("datasetid") String datasetId,
            @RequestParam(required = false) String message) {
        model.addAttribute("datasetId", datasetId);
        BreadCrumbs.set(model, "Edit dataset");
        Dataset dataset = datasetService.getById(datasetId);
        model.addAttribute("dataset", dataset);

        if (message != null) model.addAttribute("message", message);
        return "datasetEdit";
    }

    /**
     * Save user record to database.
     *
     * @param user
     * @param bindingResult
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping(value = "/{datasetid}/edit", method = RequestMethod.POST)
    public String editDatasetPost(
            Dataset dataset,
            BindingResult bindingResult, ModelMap model) {
        datasetService.update(dataset);
        model.addAttribute("message", "Test " + dataset.getTitle() + " updated");
        return "redirect:view";
    }

    /**
     * Add RDF configuration.
     * FIXME: The baseURI shall be specified at <em>runtime</em>. The vocabulary shall be based on the datasetId.
     * This is because the user shall be able to generate RDF of the latest edition with the dataset id as the base URI,
     * but shall also be able to generate RDF of specific editions.
     */
    @RequestMapping(value = "/{datasetid}/addrdf")
    public String addRdf(
            @PathVariable("datasetid") String datasetId,
            HttpServletRequest request) throws IOException, SQLException {
        Dataset dataset = datasetService.getById(datasetId);
        StringBuffer requestUrl = request.getRequestURL();
        String baseURI = requestUrl.substring(0, requestUrl.length() - "addrdf".length());
        String vocabulary = baseURI;
        String newRdf = extractRDFService.generateRDFConfig(dataset.getLatestEdition(), baseURI, vocabulary);
        dataset.setRdfConfiguration(newRdf);
        datasetService.update(dataset);
        return "redirect:edit";
    }
}
