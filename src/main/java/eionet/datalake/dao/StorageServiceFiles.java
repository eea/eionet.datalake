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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to store files in the file system.
 */
@Service
public class StorageServiceFiles implements StorageService {

    /**
     * The directory location where to store the uploaded files.
     */
    private String storageDir;

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    /**
     * Experimental method to show the user the max upload size.
     */
    @Override
    public long getFreeSpace() {
        return new File(storageDir).getFreeSpace();
    }

    @Override
    public void save(MultipartFile myFile, String uuidName) throws IOException {
        assert storageDir != null;
        File destination = new File(storageDir, uuidName);
        myFile.transferTo(destination);
    }


    @Override
    public InputStream getById(String uuidName) throws IOException {
        File location = new File(storageDir, uuidName);
        return new FileInputStream(location);
    }

    @Override
    public boolean deleteById(String uuidName) throws IOException {
        File location = new File(storageDir, uuidName);
        return location.delete();
    }
}
