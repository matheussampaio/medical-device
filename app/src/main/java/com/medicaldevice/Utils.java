package com.medicaldevice;

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
        char[] hexChars = new char[bytes.length * 3];

        for (int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }

        return new String(hexChars);
    }

}
