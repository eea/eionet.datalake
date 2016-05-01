package eionet.datalake.controller;

import eionet.datalake.dao.APIKeyService;
import eionet.datalake.dao.EditionsService;
import eionet.datalake.model.APIKey;
import eionet.datalake.model.Dataset;
import eionet.datalake.model.Edition;
import eionet.datalake.service.UploadService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * File operations - upload, download, delete.
 * See http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html
 * http://docs.oracle.com/javaee/6/tutorial/doc/gmhal.html
 */
@RestController
@RequestMapping("/api")
public class RestAPIController {

    @Autowired
    private EditionsService editionsService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    APIKeyService apiKeyService;

    private Log logger = LogFactory.getLog(RestAPIController.class);

    /**
     * Upload file for datalake.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadEdition(
                @RequestParam("file") MultipartFile myFile,
                @RequestParam(value = "dataset", required = true) String datasetId,
                @RequestParam(value = "apikey", required = true) String apiKey) throws IOException {

        try {
            APIKey apikeyRec = apiKeyService.getByKeyValue(apiKey);
        } catch (Exception e) {
            //TODO: log the error, send 403 return code.
            return "Unrecognised API key\n";
        }
        String uuidName = uploadService.uploadFile(myFile, datasetId);
        return uuidName + "\n";
    }
}

