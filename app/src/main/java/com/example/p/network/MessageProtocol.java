package com.example.p.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Standardized message protocol with acknowledgment support
 */
public class MessageProtocol {
    private static final String TAG = "MessageProtocol";

    // Message types
    public static final String TYPE_MESSAGE = "MESSAGE";
    public static final String TYPE_USER_INFO = "USER_INFO";
    public static final String TYPE_ACK = "ACK";
    public static final String TYPE_FILE = "FILE";
    public static final String TYPE_VOICE = "VOICE";

    // Message status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_FAILED = "FAILED";

    /**
     * Create message JSON
     */
    public static String createMessage(String messageId, String chatId, String senderId, 
                                      String senderName, String content, String messageType, 
                                      boolean isGroupMessage) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", TYPE_MESSAGE);
            json.put("messageId", messageId);
            json.put("chatId", chatId);
            json.put("senderId", senderId);
            json.put("senderName", senderName);
            json.put("content", content);
            json.put("messageType", messageType != null ? messageType : "TEXT");
            json.put("timestamp", System.currentTimeMillis());
            json.put("isGroupMessage", isGroupMessage);
            json.put("requiresAck", true);
            return json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error creating message", e);
            return null;
        }
    }

    /**
     * Create acknowledgment message
     */
    public static String createAck(String messageId, String status) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", TYPE_ACK);
            json.put("messageId", messageId);
            json.put("status", status);
            json.put("timestamp", System.currentTimeMillis());
            return json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error creating ACK", e);
            return null;
        }
    }

    /**
     * Create file message
     */
    public static String createFileMessage(String messageId, String chatId, String senderId,
                                          String senderName, String fileName, String fileType,
                                          long fileSize, int chunkIndex, int totalChunks,
                                          String chunkData, boolean isGroupMessage) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", TYPE_FILE);
            json.put("messageId", messageId);
            json.put("chatId", chatId);
            json.put("senderId", senderId);
            json.put("senderName", senderName);
            json.put("fileName", fileName);
            json.put("fileType", fileType);
            json.put("fileSize", fileSize);
            json.put("chunkIndex", chunkIndex);
            json.put("totalChunks", totalChunks);
            json.put("chunkData", chunkData);
            json.put("isGroupMessage", isGroupMessage);
            json.put("timestamp", System.currentTimeMillis());
            return json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error creating file message", e);
            return null;
        }
    }

    /**
     * Parse message and extract type
     */
    public static String getMessageType(String jsonMessage) {
        try {
            JSONObject json = new JSONObject(jsonMessage);
            return json.optString("type", "");
        } catch (JSONException e) {
            return "";
        }
    }

    /**
     * Parse message and extract message ID
     */
    public static String getMessageId(String jsonMessage) {
        try {
            JSONObject json = new JSONObject(jsonMessage);
            return json.optString("messageId", "");
        } catch (JSONException e) {
            return "";
        }
    }

    /**
     * Check if message requires acknowledgment
     */
    public static boolean requiresAck(String jsonMessage) {
        try {
            JSONObject json = new JSONObject(jsonMessage);
            return json.optBoolean("requiresAck", false);
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Validate message format
     */
    public static boolean isValidMessage(String jsonMessage) {
        try {
            JSONObject json = new JSONObject(jsonMessage);
            return json.has("type") && json.has("messageId");
        } catch (JSONException e) {
            return false;
        }
    }
}
