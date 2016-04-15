package eionet.datalake.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HumaneTest {

    @Test
    public void nosize() {
        assertEquals("0", Humane.humaneSize(0L));
    }

    @Test
    public void singleDigit() {
        assertEquals("9", Humane.humaneSize(9L));
    }

    @Test
    public void underThousand() {
        assertEquals("145", Humane.humaneSize(145L));
    }

    @Test
    public void kilo() {
        String expected = String.format("%.2f KB", 1.23);
        assertEquals(expected, Humane.humaneSize(1234L));
    }

    @Test
    public void kiloRoundUp() {
        String expected = String.format("%.2f KB", 1.46);
        assertEquals(expected, Humane.humaneSize(1456L));
    }

    @Test
    public void kilo10() {
        String expected = String.format("%.1f KB", 14.6);
        assertEquals(expected, Humane.humaneSize(14560L));
    }

    @Test
    public void kilo100() {
        assertEquals("146 KB", Humane.humaneSize(145600L));
    }

    @Test
    public void mega() {
        String expected = String.format("%.2f MB", 1.46);
        assertEquals(expected, Humane.humaneSize(1456000L));
    }

    @Test
    public void mega10() {
        String expected = String.format("%.1f MB", 14.6);
        assertEquals(expected, Humane.humaneSize(14560000L));
    }

    @Test
    public void giga() {
        String expected = String.format("%.2f GB", 1.46);
        assertEquals(expected, Humane.humaneSize(1456000000L));
    }

    @Test
    public void giga100() {
        assertEquals("146 GB", Humane.humaneSize(145600000000L));
    }

}
