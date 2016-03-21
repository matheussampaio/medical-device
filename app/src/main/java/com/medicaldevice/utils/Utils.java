package com.medicaldevice.utils;

import org.apache.commons.codec.binary.BinaryCodec;

public class Utils {

    public static byte[] hexStringToByteArray(String s[]) {
        byte[] bytes = new byte[s.length];

        for (int i = 0; i < s.length; i++) {
            bytes[i] = Byte.decode(s[i]);
        }

        return bytes;
    }

    public static String bytesToBinaryString(byte[] bytes) {
        return BinaryCodec.toAsciiString(bytes);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHexString(byte[] bytes) {
        return bytesToHexString(bytes, false);
    }

    public static String bytesToHexString(byte[] bytes, boolean withSpace) {
        int mult = withSpace ? 3 : 2;

        char[] hexChars = new char[bytes.length * mult];

        for (int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * mult] = hexArray[v >>> 4];
            hexChars[j * mult + 1] = hexArray[v & 0x0F];

            if (withSpace) {
                hexChars[j * mult + 2] = ' ';
            }
        }

        return new String(hexChars);
    }

}
