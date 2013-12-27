package com.kirgor.enklib.common;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Contains methods related to string encoding.
 * <p/>
 * Uses <a href="http://commons.apache.org/proper/commons-codec/">Apache Common Codec library</a>.
 */
public abstract class EncodingUtils {
    /**
     * Converts byte array to a hex string (for example, "0d246a9e").
     */
    public static String bytesToHex(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    /**
     * Converts hex string (for example, "0d246a9e") to byte array.
     */
    public static byte[] hexToBytes(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }
}
