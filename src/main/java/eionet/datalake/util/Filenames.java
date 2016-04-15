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

/**
 * Operations on file names
 */
public class Filenames {

    /**
     * Prevent instantiation.
     */
    private Filenames() {
    }

    /**
     * Remove P:\SRD-3\Temporary\, A:\, /home/fido/ etc.
     */
    public static String removePath(String pathName) {
        if (pathName == null) {
            return pathName;
        }
        return pathName.substring(Math.max(
                Math.max(pathName.lastIndexOf('\\'), pathName.lastIndexOf('/')),
                pathName.lastIndexOf(':')) + 1);
    }
}
