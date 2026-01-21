package com.example.p.utils;

import android.content.Context;
import android.provider.Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeviceUtils {
    public static String getDeviceId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), 
            Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String generateUserId(String mobileNumber, String deviceId) {
        try {
            String combined = mobileNumber + "_" + deviceId;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(combined.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 16); // Use first 16 chars
        } catch (NoSuchAlgorithmException e) {
            return mobileNumber + "_" + deviceId.substring(0, 8);
        }
    }
}
