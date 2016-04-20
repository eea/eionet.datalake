package eionet.datalake.model;

import java.sql.Timestamp;

/**
 * Test results.
 */
public class TestResult {

    private String editionId;
    private Integer testId;
    private Boolean passed;
    private Timestamp testRan;
    private String result;
    private String testType;
    private String query;

    public String getEditionId() {
        return editionId;
    }

    public void setEditionId(String editionId) {
        this.editionId = editionId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public Timestamp getTestRan() {
        return testRan;
    }

    public void setTestRan(Timestamp testRan) {
        this.testRan = testRan;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
