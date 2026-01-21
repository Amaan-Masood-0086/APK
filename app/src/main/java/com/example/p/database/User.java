package com.example.p.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    private String userId; // Mobile number or unique ID
    
    private String name;
    private String mobileNumber;
    private String deviceId;
    private String ipAddress;
    private long lastSeen;
    private boolean isOnline;

    public User() {
    }

    public User(String userId, String name, String mobileNumber, String deviceId) {
        this.userId = userId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.lastSeen = System.currentTimeMillis();
        this.isOnline = false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
