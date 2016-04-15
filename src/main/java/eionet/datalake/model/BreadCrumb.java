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

/**
 * This class implements one breadcrumb.
 */
public class BreadCrumb {

    /** link to place in hierarchy. Can be null for last crumb. */
    private String link;
    /** The text for the link. */
    private String linktext;

    public BreadCrumb(String link, String linktext) {
        this.link = link;
        this.linktext = linktext;
    }

    public String getLink() {
        return link;
    }

    /**
     * Return the breadcrumb link text, but truncate it if it is longer than 25 characters.
     */
    public String getLinktext() {
        if (linktext == null || "".equals(linktext)) {
            return "Unknown";
        } else if (linktext.length() >= 25) {
            return linktext.substring(0, 24) + "..."; //"…";
        } else {
            return linktext;
        }
    }

}
