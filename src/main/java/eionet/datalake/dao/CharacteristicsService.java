package eionet.datalake.dao;

import java.util.List;
import java.util.Date;

import eionet.datalake.model.TwoLevel;

/**
 * Service to store characteristics of editions.
 */
public interface CharacteristicsService {

    /**
     * Save the metadata for a dataset.
     */
    void insertTables(String editionId, List<String> tables);

    void insertColumns(String editionId, List<TwoLevel> columns);

    List<String> getTablesForEdition(String editionId);

    List<TwoLevel> getColumnsForEdition(String editionId);

    /**
     * Delete metadata for edition.
     */
    void deleteByEditionId(String editionId);
}
