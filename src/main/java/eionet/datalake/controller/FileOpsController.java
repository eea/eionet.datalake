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
import eionet.datalake.model.Upload;
import eionet.datalake.util.BreadCrumbs;
import eionet.datalake.util.Filenames;
import eionet.datalake.util.UniqueId;
import java.io.InputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * File operations - upload, download, delete.
 * See http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html
 * http://docs.oracle.com/javaee/6/tutorial/doc/gmhal.html
 */
@Controller
public class FileOpsController {

    @Autowired
    private UploadsService uploadsService;

    private Log logger = LogFactory.getLog(FileOpsController.class);

    /**
     * Form for uploading a file.
     */
    @RequestMapping(value = "/fileupload")
    public String fileUpload(Model model) {
        String pageTitle = "Transfer file";
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "fileupload";
    }

    /**
     * Upload file for datalake.
     */
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    public String importFile(@RequestParam("file") MultipartFile myFile,
                        final RedirectAttributes redirectAttributes,
                        final HttpServletRequest request) throws IOException {

        if (myFile == null || myFile.getOriginalFilename() == null) {
            redirectAttributes.addFlashAttribute("message", "Select a file to upload");
            return "redirect:fileupload";
        }
        String uuidName = storeFile(myFile);
        redirectAttributes.addFlashAttribute("uuid", uuidName);
        StringBuffer requestUrl = request.getRequestURL();
        redirectAttributes.addFlashAttribute("url", requestUrl.substring(0, requestUrl.length() - "/fileupload".length()));
        return "redirect:fileupload";
        //return "redirect:uploadSuccess";
    }

    /**
     * AJAX Upload file for datalake.
     */
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST, params="ajaxupload=1")
    public void importFileWithAJAX(@RequestParam("file") MultipartFile myFile,
                        HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (myFile == null || myFile.getOriginalFilename() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Select a file to upload");
            return;
        }
        String uuidName = storeFile(myFile);
        response.setContentType("text/xml");
        PrintWriter printer = response.getWriter();
        StringBuffer requestUrl = request.getRequestURL();
        String url = requestUrl.substring(0, requestUrl.length() - "/fileupload".length());
        printer.println("<?xml version='1.0'?>");
        printer.println("<package>");
        printer.println("<datasetLink>" + url + "/datasets/" + uuidName + "</datasetLink>");
        printer.println("</package>");
        printer.flush();
        response.flushBuffer();
    }

    /*
     * Store file with a generated unique id and unique family id.
     */
    private String storeFile(MultipartFile myFile) throws IOException {
        String familyId = UniqueId.generateUniqueId();
        return storeFile(myFile, familyId);
    }

    /*
     * Store file with a generated unique id and specified family id.
     */
    private String storeFile(MultipartFile myFile, String familyID) throws IOException {
        String uniqueId = UniqueId.generateUniqueId();
        uploadsService.storeFile(myFile, uniqueId, familyID);
        return uniqueId;
    }

    /**
     * Page to show upload success.
     */
    /*
    @RequestMapping(value = "/uploadSuccess")
    public String uploadResult(Model model, HttpServletRequest request) {
        String pageTitle = "File uploaded";
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        StringBuffer requestUrl = request.getRequestURL();
        model.addAttribute("url", requestUrl.substring(0, requestUrl.length() - "/uploadSuccess".length()));
        return "uploadSuccess";
    }
    */

    /**
     * Download a file.
     */
    @RequestMapping(value = "/datasets/{file_name}/download", method = RequestMethod.GET)
    public void downloadFile(
        @PathVariable("file_name") String fileId, HttpServletResponse response) throws IOException {

        Upload uploadRec = uploadsService.getById(fileId);
        response.setContentType("application/octet-stream");
        //response.setContentLength(uploadRec.getSize()); // Too small - max 2.1 GB.
        response.setHeader("Content-Length", Long.toString(uploadRec.getSize()));
        response.setHeader("Content-Disposition", "attachment; filename=" + uploadRec.getFilename());

        InputStream is = uploadRec.getContentAsStream();
        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
        is.close();
    }

    @RequestMapping(value = "/delete/{file_name}")
    public String deleteFile(
        @PathVariable("file_name") String fileId, final Model model) throws IOException {
        model.addAttribute("uuid", fileId);
        return "deleteConfirmation";
    }

    /**
     * Delete files by uuid.
     *
     * @param ids - list of uuids
     */
    @RequestMapping(value = "/deletefiles", method = RequestMethod.POST)
    public String deleteFiles(@RequestParam("id") List<String> ids,
            final RedirectAttributes redirectAttributes) throws IOException {
        uploadsService.deleteFiles(ids);
        redirectAttributes.addFlashAttribute("message", "File(s) deleted");
        return "redirect:/";
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND) //, reason = "File not found")
    public String filenotFoundError(HttpServletRequest req, Exception exception) {
        return "filenotfound";
    }
}

