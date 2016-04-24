package eionet.datalake.dao;

import java.util.List;
import java.util.Date;

import eionet.datalake.model.QATest;

/**
 * Service to store qa tests.
 */
public interface QATestService {

    /**
     * Save the metadata for a qatest.
     */
    void save(QATest qatest);

    /**
     * Fetch the metadata for one qatest.
     */
    QATest getById(String id);

    /**
     * Delete metadata for file by Id.
     */
    void deleteById(String id);

    void deleteTests(List<String> testIds);

    /**
     * Get all records.
     */
    List<QATest> getByDatasetId(String datasetId);

    /**
     * Delete all metadata for all qa tests. Mainly used for testing.
     */
    void deleteAll();
}
