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
