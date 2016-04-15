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

import eionet.datalake.model.Upload;

/**
 * Service to store metadata for uploaded files.
 */
public interface MetadataService {

    /**
     * Save the metadata for an upload.
     */
    void save(Upload upload);

    /**
     * Fetch the metadata for one upload.
     */
    Upload getById(String id);

    /**
     * Delete metadata for file by Id.
     */
    void deleteById(String id);

    /**
     * Get a list of all files where the expiration date has passed.
     */
    List<String> getExpired();

    /**
     * Get a list of all files where the expiration date has passed.
     *
     * @param expirationDate - The cut-off date.
     */
    List<String> getExpired(Date expirationDate);

    /**
     * Get a list of all files where the expiration date has not passed.
     */
    List<Upload> getUnexpired();

    /**
     * Get a list of all files where the expiration date has not passed.
     *
     * @param expirationDate - The cut-off date.
     */
    List<Upload> getUnexpired(Date expirationDate);

    /**
     * Get all records.
     */
    List<Upload> getAll();

    /**
     * Delete all metadata for all uploads. Mainly used for testing.
     */
    void deleteAll();
}
