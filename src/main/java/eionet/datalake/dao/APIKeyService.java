package eionet.datalake.dao;

import java.util.List;
import java.util.Date;

import eionet.datalake.model.APIKey;

/**
 * Service to store api keys.
 */
public interface APIKeyService {

    /**
     * Save the metadata for a dataset.
     */
    void insert(APIKey record);

    /**
     * Fetch the metadata for one dataset.
     */
    APIKey getById(String id);

    APIKey getByKeyValue(String keyValue);
    /**
     * Delete APIKey for file by Id.
     */
    void deleteById(String id);

    void deleteAll();

    List<APIKey> getAll();

    void update(APIKey record);
}
