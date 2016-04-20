package eionet.datalake.model;

import eionet.datalake.util.Humane;
import java.io.InputStream;
import java.sql.Timestamp;

/**
 * Management and metadata for uploaded files. Contains original file name
 * upload date etc.
 */
public class Edition {

    private String editionId;
    private String filename;
    private String uploader;
    private String contentType;
    private long fileSize;
    private InputStream content;
    /** All editions of the same dataset has the same family id. */
    private String familyId;
    /** The upload time determines which edition is the newest. */
    private Timestamp uploadTime;

    public String getEditionId() {
        return editionId;
    }

    public void setEditionId(String editionId) {
        this.editionId = editionId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(final long size) {
        this.fileSize = size;
    }

    public InputStream getContentAsStream() {
        return content;
    }

    public void setContentStream(final InputStream content) {
        this.content = content;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    /**
     * Return the file size in a human readable way.
     */
    public String getHumaneSize() {
        return Humane.humaneSize(fileSize);
    }
}
