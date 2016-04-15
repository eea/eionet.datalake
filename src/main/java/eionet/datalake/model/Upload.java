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
import java.util.Date;

/**
 * Management and metadata for uploaded files. Contains original file name
 * expiration date etc.
 */
public class Upload {

    private String id;
    private Date expires;
    private String filename;
    private String uploader;
    private String contentType;
    private long fileSize;
    private InputStream content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
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

    public long getSize() {
        return fileSize;
    }

    public void setSize(final long size) {
        this.fileSize = size;
    }

    public InputStream getContentAsStream() {
        return content;
    }

    public void setContentStream(final InputStream content) {
        this.content = content;
    }

    public String getHumaneSize() {
        return Humane.humaneSize(fileSize);
    }
}
