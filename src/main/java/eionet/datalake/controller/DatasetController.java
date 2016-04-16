/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Transfer 1.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. All Rights Reserved.
 *
 * Contributor(s):
 *        Søren Roug
 */
package eionet.datalake.controller;

import eionet.datalake.dao.UploadsService;
import eionet.datalake.dao.SQLService;
import eionet.datalake.model.Upload;
import eionet.datalake.util.BreadCrumbs;
import eionet.datalake.util.Humane;
import java.io.IOException;
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
public class DatasetController {

    @Autowired
    private UploadsService uploadsService;

    @Autowired
    private SQLService sqlService;

    @RequestMapping(value = "/datasets")
    public String listDatasets(Model model) {
        String pageTitle = "Dataset List";

        List<Upload> datasets = uploadsService.getAll();
        model.addAttribute("datasets", datasets);
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "datasets";
    }

    /**
     * Dataset query page
     */
    @RequestMapping(value = "/datasets/{uuid}/query")
    public String datasetQuery(
            @PathVariable("uuid") String fileId, final Model model) throws IOException {
        Upload dataset = uploadsService.getById(fileId);
        model.addAttribute("uuid", fileId);
        model.addAttribute("dataset", dataset);
        return "datasetQuery";
    }

    /**
     * Dataset query page
     */
    @RequestMapping(value = "/datasets/{uuid}/query", method = RequestMethod.POST)
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
     * Dataset
     */
    @RequestMapping(value = "/datasets/{uuid}")
    public String datasetFactsheet(
            @PathVariable("uuid") String fileId, final Model model) throws Exception {
        Upload dataset = uploadsService.getById(fileId);
        List<String> tables = sqlService.metaTables(fileId);
        model.addAttribute("uuid", fileId);
        model.addAttribute("dataset", dataset);
        model.addAttribute("tables", tables);
        return "datasetFactsheet";
    }

}
