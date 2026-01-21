package com.example.p.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups")
public class Group {
    @PrimaryKey
    private String groupId;
    
    private String groupName;
    private String description;
    private String adminId; // Creator/Admin user ID
    private boolean adminOnlyMessages; // If true, only admins can send messages
    private long createdAt;
    private String groupImagePath;

    public Group() {
        this.createdAt = System.currentTimeMillis();
        this.adminOnlyMessages = false;
    }

    public Group(String groupId, String groupName, String adminId) {
        this();
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminId = adminId;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public boolean isAdminOnlyMessages() {
        return adminOnlyMessages;
    }

    public void setAdminOnlyMessages(boolean adminOnlyMessages) {
        this.adminOnlyMessages = adminOnlyMessages;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroupImagePath() {
        return groupImagePath;
    }

    public void setGroupImagePath(String groupImagePath) {
        this.groupImagePath = groupImagePath;
    }
}
