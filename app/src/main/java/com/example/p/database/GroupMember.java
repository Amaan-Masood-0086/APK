package com.example.p.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "group_members",
    foreignKeys = {
        @ForeignKey(
            entity = Group.class,
            parentColumns = "groupId",
            childColumns = "groupId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "userId",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("groupId"), @Index("userId")}
)
public class GroupMember {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String groupId;
    private String userId;
    private boolean isAdmin;
    private long joinedAt;

    public GroupMember() {
        this.joinedAt = System.currentTimeMillis();
        this.isAdmin = false;
    }

    public GroupMember(String groupId, String userId, boolean isAdmin) {
        this();
        this.groupId = groupId;
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }
}
