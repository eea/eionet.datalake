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

    /* The following are stored in the editions table. */
    private Timestamp uploadTime;


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

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }
}
