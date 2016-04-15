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

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold user and roles.
 */
public class Authorisation {

    private String userId;

    /** List of roles. */
    private List<String> authorisations;

    /**
     * Constructor of empty object.
     */
    public Authorisation() {
        this.authorisations = new ArrayList<String>();
    }

    /**
     * Constructor.
     */
    public Authorisation(String userId, List<String> authorisations) {
        this.userId = userId;
        this.authorisations = authorisations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getAuthorisations() {
        return authorisations;
    }

    public void setAuthorisations(List<String> authorisations) {
        this.authorisations = authorisations;
    }

}
