package com.medicaldevice.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.codec.binary.BinaryCodec;

/**
 * Utils
 */
public class Utils {

    private static final String TAG = "MEDICAL_DEVICE_UTILS";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Convert hex string array to byte array.
     * @param s Hex string array.
     * @return Byte array.
     */
    public static byte[] hexStringToByteArray(String s[]) {
        byte[] bytes = new byte[s.length];

        for (int i = 0; i < s.length; i++) {
            bytes[i] = Byte.decode(s[i]);
        }

        return bytes;
    }

    /**
     * Convert bytes array to binary string.
     * @param bytes Byte array.
     * @return Binary string.
     */
    public static String bytesToBinaryString(byte[] bytes) {
        return BinaryCodec.toAsciiString(bytes);
    }

    /**
     * Convert byte to hex string.
     * @param b Bytes.
     * @return Hex string.
     */
    public static String bytesToHexString(byte b) {
        return bytesToHexString(b, false);
    }

    /**
     * Convert bytes to hex string.
     * @param b Bytes.
     * @param withSpace If true, add space between results.
     * @return Hex string.
     */
    public static String bytesToHexString(byte b, boolean withSpace) {
        byte[] bytes = new byte[1];

        bytes[0] = b;

        return bytesToHexString(bytes, withSpace);
    }

    /**
     * Convert Bytes to Hex string.
     * @param bytes Bytes.
     * @param withSpace If true, add space between result.
     * @return
     */
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

    /**
     * Convert Hex to ASCII chars.
     * @param hexString Hex string.
     * @return ASCII string.
     */
    public static String hexToString(String hexString) {
        StringBuilder output = new StringBuilder();

        hexString = hexString.replaceAll("\\s+", "");

        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    /**
     * Check if internet if available.
     * @param context Activity context.
     * @return True if internet available, false otherwise.
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager networkConnection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = networkConnection.getActiveNetworkInfo();

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        if (activeNetworkInfo != null) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                haveConnectedWifi = true;
            }

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                haveConnectedMobile = true;
            }
        }

        return haveConnectedWifi || haveConnectedMobile;
    }
}
