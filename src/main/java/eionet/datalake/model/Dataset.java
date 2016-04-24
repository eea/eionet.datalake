package eionet.datalake.model;

import java.sql.Timestamp;

/**
 * Datasets.
 */
public class Dataset {

    private String datasetId;
    private String title;
    private Integer keep;
    private Integer keepFailures;
    private String latestEdition;
    private String rdfConfiguration;

    /* The following are stored in the editions table. */
    private Timestamp uploadTime;
    private Integer countTests;
    private Integer countFailures;
    private String filename;


    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    @Deprecated
    public String getFamilyId() {
        return datasetId;
    }

    @Deprecated
    public void setFamilyId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getKeep() {
        return keep;
    }

    public void setKeep(Integer keep) {
        this.keep = keep;
    }

    public Integer getKeepFailures() {
        return keepFailures;
    }

    public void setKeepFailures(Integer keepFailures) {
        this.keepFailures = keepFailures;
    }

    public String getLatestEdition() {
        return latestEdition;
    }

    public void setLatestEdition(String latestEdition) {
        this.latestEdition = latestEdition;
    }

    public String getRdfConfiguration() {
        return rdfConfiguration;
    }

    public void setRdfConfiguration(String rdfConfiguration) {
        this.rdfConfiguration = rdfConfiguration;
    }

    /* The following are stored in the editions table. */
    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getCountTests() {
        return countTests;
    }

    public void setCountTests(Integer countTests) {
        this.countTests = countTests;
    }

    public Integer getCountFailures() {
        return countFailures;
    }

    public void setCountFailures(Integer countFailures) {
        this.countFailures = countFailures;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
