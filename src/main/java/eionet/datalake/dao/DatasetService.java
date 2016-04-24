package eionet.datalake.dao;

import java.util.List;
import java.util.Date;

import eionet.datalake.model.Dataset;

/**
 * Service to store datasets.
 */
public interface DatasetService {

    /**
     * Save the metadata for a dataset.
     */
    void save(Dataset dataset);

    /**
     * Fetch the metadata for one dataset.
     */
    Dataset getById(String id);

    /**
     * Delete metadata for file by Id.
     */
    void deleteById(String id);

    /**
     * Delete all metadata for all qa tests. Mainly used for testing.
     */
    void deleteAll();

    /**
     * Updates the latestedition field in datasets to the latest edition uploaded.
     */
    void updateToLatest(String datasetId);

    List<Dataset> getAll();

    void update(Dataset datasetRec);
}
