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
package eionet.datalake.model;

import eionet.datalake.util.Humane;
import java.io.InputStream;
import java.sql.Timestamp;

/**
 * Management and metadata for uploaded files. Contains original file name
 * upload date etc.
 */
public class Upload {

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
