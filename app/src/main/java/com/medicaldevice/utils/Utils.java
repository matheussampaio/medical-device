package com.medicaldevice.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.codec.binary.BinaryCodec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final String TAG = "MEDICAL_DEVICE_UTILS";

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

    public static String bytesToHexString(byte b) {
        return bytesToHexString(b, false);
    }

    public static String bytesToHexString(byte b, boolean withSpace) {
        byte[] bytes = new byte[1];

        bytes[0] = b;

        return bytesToHexString(bytes, withSpace);
    }

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

    public static String hexToString(String hexString) {
        StringBuilder output = new StringBuilder();

        hexString = hexString.replaceAll("\\s+", "");

        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
    public static long getDate(String sDate, String sTime){
        SimpleDateFormat ft =
                new SimpleDateFormat("mm/dd/yy HH:mm:ss");
        Date date = null;
        try {
            date = ft.parse(sDate+" "+sTime);
        } catch (ParseException exception) {
            Logger.e(TAG,exception.getStackTrace().toString());
        }
        return date.getTime();
    }

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
