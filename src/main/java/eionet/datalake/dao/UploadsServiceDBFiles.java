package eionet.datalake.dao;

import eionet.datalake.model.Upload;
import eionet.datalake.controller.FileNotFoundException;
import eionet.datalake.util.BreadCrumbs;
import eionet.datalake.util.Filenames;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Store a file and its meta data. Face to storage service and metadata service.
 */
@Service
public class UploadsServiceDBFiles implements UploadsService {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private StorageService storageService;

    private Log logger = LogFactory.getLog(UploadsServiceDBFiles.class);

    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void storeFile(MultipartFile myFile, String uuidName, String familyId) throws IOException {
        storageService.save(myFile, uuidName);
        Upload rec = new Upload();
        rec.setEditionId(uuidName);
        rec.setFilename(Filenames.removePath(myFile.getOriginalFilename()));
        rec.setContentType(myFile.getContentType());
        rec.setFileSize(myFile.getSize());
        rec.setFamilyId(familyId);
        String userName = getUserName();
        rec.setUploader(userName);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        rec.setUploadTime(ts);

        metadataService.save(rec);
        logger.info("Uploaded: " + myFile.getOriginalFilename() + " by " + userName);
    }

    /**
     * Helper method to get authenticated userid.
     */
    private String getUserName() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "UNAUTHENTICATED";
            //throw new IllegalArgumentException("Not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Download a file.
     */
    @Override
    public Upload getById(String fileId) throws IOException {

        Upload uploadRec;
        try {
            uploadRec = metadataService.getById(fileId);
        } catch (Exception e) {
            throw new FileNotFoundException(fileId);
        }
        uploadRec.setContentStream(storageService.getById(fileId));
        return uploadRec;
    }

    @Override
    public boolean deleteById(String fileId) throws IOException {
        metadataService.deleteById(fileId);
        return storageService.deleteById(fileId);
    }

    @Override
    public void deleteFiles(List<String> ids) throws IOException {
        for (String fileId : ids) {
            metadataService.deleteById(fileId);
            storageService.deleteById(fileId);
        }
    }

    @Override
    public List<Upload> getByFamilyId(String familyId) {
        return metadataService.getByFamilyId(familyId);
    }

    @Override
    public List<Upload> getAll() {
        return metadataService.getAll();
    }

    @Override
    public long getFreeSpace() {
        return storageService.getFreeSpace();
    }

}

