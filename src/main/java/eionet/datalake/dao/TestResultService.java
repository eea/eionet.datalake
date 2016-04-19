package eionet.datalake.dao;

import java.util.List;

import eionet.datalake.model.TestResult;

/**
 * Service to store qa tests.
 */
public interface TestResultService {

    /**
     * Replace the metadata for a test result.
     */
    void replace(TestResult qatest);
    /**
     * Save the metadata for a test result.
     */
    void save(TestResult qatest);

    /**
     * Fetch the metadata for one qatest.
     */
    TestResult getOneById(Integer testId, String editionId);

    /**
     * Delete metadata for file by Id.
     */
    void deleteOneById(Integer testId, String editionId);

    /**
     * Get all records.
     */
    List<TestResult> getByEditionId(String editionId);

    /**
     * Delete all metadata for all qa tests. Mainly used for testing.
     */
    void deleteAll();
}
