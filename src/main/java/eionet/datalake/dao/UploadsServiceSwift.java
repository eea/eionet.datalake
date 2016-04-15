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

import eionet.datalake.controller.FileNotFoundException;
import eionet.datalake.model.Upload;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.exception.NotFoundException;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.DirectoryOrObject;
import org.javaswift.joss.model.StoredObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to store files in the Swift Object Store.
 */
@Service
public class UploadsServiceSwift implements UploadsService {

    private String swiftAuthUrl;

    private String swiftTenantId;

    /** * The swift location where to store the uploaded files.  */
    private String swiftContainer = "datalake";

    private String swiftUsername;

    private String swiftPassword;

    /** Set to the string "true" if testing. */
    private String swiftMock;

    private AccountConfig config;

    private Account account;

    private Container container;

    private Log logger = LogFactory.getLog(UploadsServiceSwift.class);

    public void setSwiftAuthUrl(String swiftAuthUrl) {
        this.swiftAuthUrl = swiftAuthUrl;
    }

    public void setSwiftTenantId(String swiftTenantId) {
        this.swiftTenantId = swiftTenantId;
    }

    public void setSwiftContainer(String swiftContainer) {
        this.swiftContainer = swiftContainer;
    }

    public void setSwiftUsername(String swiftUsername) {
        this.swiftUsername = swiftUsername;
    }

    public void setSwiftPassword(String swiftPassword) {
        this.swiftPassword = swiftPassword;
    }

    public void setSwiftMock(String swiftMock) {
        this.swiftMock = swiftMock;
    }

    /**
     * Logs into the Object store, but note that the token only works for 24 hours.
     * FIXME: Test
     */
    private void login() {
        if (swiftUsername == null || swiftPassword == null || swiftAuthUrl == null) {
            System.out.println("ERROR: Swift storage account is not configured");
            logger.error("Swift storage account is not configured");
        }
        config = new AccountConfig();
        config.setUsername(swiftUsername);
        config.setPassword(swiftPassword);
        config.setAuthUrl(swiftAuthUrl);
        config.setTenantId(swiftTenantId);
//      config.setTenantName(swiftTenantName);
        if (swiftMock != null) {
            config.setMock(Boolean.valueOf(swiftMock));
        }
        account = new AccountFactory(config).createAccount();
        container = account.getContainer(swiftContainer);
        if (!container.exists()) {
            container.create();
        }
        //container.makePublic();
    }

// https://github.com/javaswift/joss/blob/master/src/main/java/org/javaswift/joss/model/StoredObject.java
    @Override
    public void storeFile(MultipartFile myFile, String fileId, int fileTTL) throws IOException {
        if (swiftUsername == null) {
            System.out.println("Swift username is not configured");
        }
        assert swiftUsername != null;
        if (config == null) {
            login();
        }
        StoredObject swiftObject = container.getObject(fileId);
        swiftObject.uploadObject(myFile.getInputStream());
        if (myFile.getContentType() != null) {
            swiftObject.setContentType(myFile.getContentType());
        }

        Map<String, Object> metadata = new HashMap<String, Object>();
        if (myFile.getOriginalFilename() != null) {
            metadata.put("filename", myFile.getOriginalFilename());
        }
        if (myFile.getContentType() != null) {
            metadata.put("content-type", myFile.getContentType());
        }
        swiftObject.setMetadata(metadata);
        swiftObject.saveMetadata();
        //swiftObject.setDeleteAt(Date date);
    }


    @Override
    public Upload getById(String fileId) throws IOException {
        if (config == null) {
            login();
        }
        Upload uploadRec = new Upload();
        StoredObject swiftObject = null;
        try {
            swiftObject = container.getObject(fileId);
        } catch (Exception e) {
            throw new FileNotFoundException(fileId);
        }
        Map<String, Object> metadata = swiftObject.getMetadata();
        uploadRec.setFilename((String) metadata.get("filename"));
        uploadRec.setContentType((String) metadata.get("content-type"));
        uploadRec.setSize(swiftObject.getContentLength());
        uploadRec.setContentStream(swiftObject.downloadObjectAsInputStream());
        return uploadRec;
    }

    @Override
    public boolean deleteById(String fileId) throws IOException {
        try {
            StoredObject swiftObject = container.getObject(fileId);
            swiftObject.delete();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public void deleteFiles(List<String> ids) throws IOException {
        for (String fileId : ids) {
            deleteById(fileId);
        }
    }

    @Override
    public List<Upload> getAll() {
        //FIXME: Implement
        Collection<DirectoryOrObject> c = container.listDirectory();
        return new ArrayList<Upload>(0);
    }

    @Override
    public long getFreeSpace() {
        return 0L;
    }
}
