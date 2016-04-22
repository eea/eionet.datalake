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
