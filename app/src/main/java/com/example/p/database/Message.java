package com.example.p.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "messages")
@TypeConverters({Converters.class})
public class Message {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String messageId; // Unique message ID
    private String chatId; // User ID or Group ID
    private String senderId;
    private String senderName;
    private String content;
    private String messageType; // TEXT, VOICE, IMAGE, PDF, FILE
    private String filePath; // For files, voice, images
    private long timestamp;
    private String status; // PENDING, SENT, DELIVERED, FAILED
    private boolean isGroupMessage;
    private long createdAt;

    public Message() {
        this.createdAt = System.currentTimeMillis();
        this.timestamp = System.currentTimeMillis();
        this.status = "PENDING";
        this.messageType = "TEXT";
        this.isGroupMessage = false;
    }

    public Message(String chatId, String senderId, String senderName, String content) {
        this();
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.messageId = generateMessageId();
    }

    private String generateMessageId() {
        return senderId + "_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isGroupMessage() {
        return isGroupMessage;
    }

    public void setGroupMessage(boolean groupMessage) {
        isGroupMessage = groupMessage;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
