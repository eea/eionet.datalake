/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Transfer 1.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. All Rights Reserved.
 *
 * Contributor(s):
 *        Søren Roug
 */
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
