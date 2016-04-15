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
 * Human friendly representations.
 */
public class Humane {

    /**
     * Prevent instantiation.
     */
    private Humane() {
    }

    /**
     * Return a size with three significant digits and a size unit.
     */
    public static String humaneSize(long size) {
        String unit = "";
        double divisor = 1.0;
        if (size >= 1000L * 1000L * 1000L * 1000L) {
            unit = " TB";
            divisor = 0.0 + 1000L * 1000L * 1000L * 1000L;
        } else if (size >= 1000L * 1000L * 1000L) {
            unit = " GB";
            divisor = 0.0 + 1000L * 1000L * 1000L;
        } else if (size >= 1000L * 1000L) {
            unit = " MB";
            divisor =  0.0 + 1000L * 1000L;
        } else if (size >= 1000L) {
            unit = " KB";
            divisor =  0.0 + 1000L;
        } else {
            return String.valueOf(size);
        }

        String format = "%.0f";
        String rawStrVal = String.valueOf(size);
        int sizeLen = rawStrVal.length();
        if (sizeLen % 3 == 1) {
            format = "%.2f" + unit;
        } else if (sizeLen % 3 == 2) {
            format = "%.1f" + unit;
        } else {
            format = "%.0f" + unit;
        }
        Double dSize = Double.valueOf(size);
        return String.format(format, dSize / divisor);
    }

}
