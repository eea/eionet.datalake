package eionet.datalake.dao;

import eionet.datalake.model.Edition;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Backend service for storing uploads with metadata.
 */
public interface EditionsService {

    /**
     * Save file and metadata.
     */
    void storeFile(MultipartFile myFile, String uuidName, String datasetId) throws IOException;

    void updateQAScore(String editionId, int countTests, int countFailures);

    /**
     * Download a file.
     */
    Edition getById(String fileId) throws IOException;

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
    List<Edition> getByDatasetId(String datasetId);

    /**
     * Get the latest edition with 0 failures.
     */
    Edition getLatestGood(String datasetId) throws IOException;

    /**
     * Get a list of all files.
     */
    List<Edition> getAll();

    /**
     * Method to show the user the max upload size.
     */
    long getFreeSpace();

}
