package eionet.datalake.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FilenamesTest {

    @Test
    public void nopath() {
        assertEquals("testfile.txt", Filenames.removePath("testfile.txt"));
    }

    @Test
    public void diskette() {
        assertEquals("testfile.txt", Filenames.removePath("a:\\testfile.txt"));
    }

    @Test
    public void windowsPath() {
        assertEquals("eu2830okt2015_uppercaseUID.xml", Filenames.removePath("P:\\SRD-3\\Temporary\\eu2830okt2015_uppercaseUID.xml"));
    }

    @Test
    public void linuxPath() {
        assertEquals("my diary.zip", Filenames.removePath("/home/fido/.secrets/my diary.zip"));
    }

    @Test
    public void emptyString() {
        assertEquals("", Filenames.removePath(""));
    }

    @Test
    public void nullString() {
        assertEquals(null, Filenames.removePath(null));
    }
}
