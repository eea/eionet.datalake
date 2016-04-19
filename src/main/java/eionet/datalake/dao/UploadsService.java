package eionet.datalake.dao;

import eionet.datalake.model.Upload;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Backend service for storing uploads with metadata.
 */
public interface UploadsService {

    /**
     * Save file and metadata.
     */
    void storeFile(MultipartFile myFile, String uuidName, String familyId) throws IOException;

    /**
     * Download a file.
     */
    Upload getById(String fileId) throws IOException;

    /**
     * Delete file by uuid.
     *
     * @param fileId - id of file.
     */
    boolean deleteById(String fileId) throws IOException;

    /**
     * Delete files by uuid.
     *
     * @param ids - list of uuids
     */
    void deleteFiles(List<String> ids) throws IOException;

    /**
     * Get all editions of same dataset.
     */
    List<Upload> getByFamilyId(String familyId);

    /**
     * Get a list of all files.
     */
    List<Upload> getAll();

    /**
     * Method to show the user the max upload size.
     */
    long getFreeSpace();

}
