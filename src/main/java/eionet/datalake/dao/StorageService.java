package eionet.datalake.dao;

import java.io.InputStream;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to put files in storage. The storage can be file system, object database etc.
 */
public interface StorageService {

    /**
     * Store the file at a location and return a generated unique identifier for it.
     */
    void save(MultipartFile myFile, String uuidName) throws IOException;

    /**
     * Method to show the user the max upload size.
     */
    long getFreeSpace();

    /**
     * Get an open stream to the stored object.
     */
    InputStream getById(String id) throws IOException;

    /**
     * Delete a file in the storage service. If the file does not exist, then return false.
     *
     * @param id - unique identifier for the file.
     */
    boolean deleteById(String id) throws IOException;
}
