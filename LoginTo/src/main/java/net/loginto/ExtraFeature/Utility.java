package net.loginto.ExtraFeature;

import java.security.MessageDigest;
import java.util.HexFormat;

public class Utility {
    public static String sha256(String input) throws Exception {
        return HexFormat.of().formatHex(
            MessageDigest.getInstance("SHA-256").digest(input.getBytes())
        );
    }
}
