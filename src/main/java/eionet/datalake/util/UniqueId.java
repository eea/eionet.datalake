package eionet.datalake.util;

import java.util.UUID;
import java.util.Base64;

public class UniqueId {

    // To prevent initialisations.
    private UniqueId() {
    }

    /**
     * Generate Unique ID. Shorter ids, but requires Java 8.
     */
    public static String generateUniqueId() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        long lowBits = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte)((lowBits >> (i * 8)) & 0xff);
        }
        long highBits = uuid.getMostSignificantBits();
        for (int i = 0; i < 8; i++) {
            uuidBytes[i + 8] = (byte)((highBits >> (i * 8)) & 0xff);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
    }

    /*
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    */

}
