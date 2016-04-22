package eionet.datalake.dao;

import java.util.List;
import java.util.Date;

import eionet.datalake.model.Edition;

/**
 * Service to store metadata for uploaded files.
 */
public interface MetadataService {

    /**
     * Save the metadata for an upload.
     */
    void save(Edition upload);

    void updateQAScore(String editionId, int countTests, int countFailures);

    /**
     * Fetch the metadata for one upload.
     */
    Edition getById(String id);

    /**
     * Get all editions of same dataset.
     */
    List<Edition> getByFamilyId(String familyId);

    /**
     * Delete metadata for file by Id.
     */
    void deleteById(String id);

    /**
     * Get all records.
     */
    List<Edition> getAll();

    /**
     * Delete all metadata for all uploads. Mainly used for testing.
     */
    void deleteAll();
}
