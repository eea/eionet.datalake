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

import java.util.List;

import eionet.datalake.dao.UploadsService;
import eionet.datalake.model.Upload;
import eionet.datalake.util.BreadCrumbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * View datasets.
 */
@Controller
public class UploadsController {

    @Autowired
    private UploadsService uploadsService;

    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    public String findUploads(Model model) {
        String pageTitle = "Dataset List";

        List<Upload> datasets = uploadsService.getAll();
        model.addAttribute("datasets", datasets);
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "datasets";
    }

}
