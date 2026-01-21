package com.example.p.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREFS_NAME = "LinkUpPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_IP_ADDRESS = "ip_address";

    private SharedPreferences sharedPreferences;

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setUserName(String userName) {
        sharedPreferences.edit().putString(KEY_USER_NAME, userName).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public void setMobileNumber(String mobileNumber) {
        sharedPreferences.edit().putString(KEY_MOBILE_NUMBER, mobileNumber).apply();
    }

    public String getMobileNumber() {
        return sharedPreferences.getString(KEY_MOBILE_NUMBER, null);
    }

    public void setDeviceId(String deviceId) {
        sharedPreferences.edit().putString(KEY_DEVICE_ID, deviceId).apply();
    }

    public String getDeviceId() {
        return sharedPreferences.getString(KEY_DEVICE_ID, null);
    }

    public void setIpAddress(String ipAddress) {
        sharedPreferences.edit().putString(KEY_IP_ADDRESS, ipAddress).apply();
    }

    public String getIpAddress() {
        return sharedPreferences.getString(KEY_IP_ADDRESS, null);
    }

    public boolean isUserRegistered() {
        return getUserId() != null && getUserName() != null && getMobileNumber() != null;
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
