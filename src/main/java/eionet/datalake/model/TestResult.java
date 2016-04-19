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
}
