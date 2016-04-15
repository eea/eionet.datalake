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
    void storeFile(MultipartFile myFile, String uuidName, int fileTTL) throws IOException;

    /**
     * Download a file.
     */
    Upload getById(String fileId) throws IOException;

    /**
     * Return all expired items from the database.
     */
    List<String> getExpired();

    boolean deleteById(String fileId) throws IOException;

    /**
     * Delete files by uuid.
     *
     * @param ids - list of uuids
     */
    void deleteFiles(List<String> ids) throws IOException;

    /**
     * Get a list of all files where the expiration date has not passed.
     */
    List<Upload> getUnexpired();

    /**
     * Method to show the user the max upload size.
     */
    long getFreeSpace();

}

