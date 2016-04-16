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
package eionet.datalake.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.Model;
import eionet.datalake.model.BreadCrumb;

/**
 * Generate the bread crumbs.
 */
public class BreadCrumbs {

    /** Eionet portal. This should be part of the layout template - not here. */
    //private static BreadCrumb eionetCrumb;

    /** Toplevel of this site. */
    private static BreadCrumb homeCrumb;

    // To prevent initialisations.
    private BreadCrumbs() {
    }

    static {
        //eionetCrumb = new BreadCrumb("http://www.eionet.europa.eu/", "Eionet");
        homeCrumb = new BreadCrumb("/", "Datalake");
    }

    /**
     * Convenience method to set a breadcrumb on a top level page.
     */
    public static void set(Model model, String lastcrumb) {
        BreadCrumbs.set(model, new BreadCrumb(null, lastcrumb));
    }

    /**
     * Convenience method to set a breadcrumb on a page one level below.
     */
    public static void set(Model model, String parentLink, String parentText, String lastcrumb) {
        BreadCrumbs.set(model, new BreadCrumb(parentLink, parentText), new BreadCrumb(null, lastcrumb));
    }

    /**
     * Create a indefinite list of breadcrumbs.
     */
    public static void set(Model model, BreadCrumb... crumbs) {
        List<BreadCrumb> breadcrumbList = new ArrayList<BreadCrumb>();

        //breadcrumbList.add(eionetCrumb);
        breadcrumbList.add(homeCrumb);

        for (BreadCrumb crumb : crumbs) {
            breadcrumbList.add(crumb);
        }
        model.addAttribute("breadcrumbs", breadcrumbList);
    }

}
