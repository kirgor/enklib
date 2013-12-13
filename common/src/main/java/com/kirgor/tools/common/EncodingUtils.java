package com.kirgor.tools.common;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public abstract class EncodingUtils {
    public static String bytesToHex(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    public static byte[] hexToBytes(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }


}
