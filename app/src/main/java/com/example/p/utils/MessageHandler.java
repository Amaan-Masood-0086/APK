package com.example.p.utils;

import android.content.Context;
import android.util.Log;

import com.example.p.database.Message;
import com.example.p.database.User;
import com.example.p.repository.MessageRepository;
import com.example.p.repository.UserRepository;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageHandler {
    private static final String TAG = "MessageHandler";
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private UserPreferences userPreferences;
    private Context context;

    public MessageHandler(Context context) {
        this.context = context.getApplicationContext();
        this.messageRepository = new MessageRepository((android.app.Application) this.context);
        this.userRepository = new UserRepository((android.app.Application) this.context);
        this.userPreferences = new UserPreferences(context);
    }

    /**
     * Handle incoming message from socket
     * Parse JSON and save to database
     */
    public void handleIncomingMessage(String jsonMessage) {
        try {
            JSONObject json = new JSONObject(jsonMessage);
            String type = json.optString("type", "MESSAGE");

            if ("MESSAGE".equals(type)) {
                handleTextMessage(json);
            } else if ("USER_INFO".equals(type)) {
                handleUserInfo(json);
            }
        } catch (JSONException e) {
            // If not JSON, treat as plain text message (backward compatibility)
            Log.d(TAG, "Not JSON, treating as plain text");
            handlePlainTextMessage(jsonMessage);
        }
    }

    private void handleTextMessage(JSONObject json) {
        try {
            String messageId = json.optString("messageId");
            String chatId = json.optString("chatId");
            String senderId = json.optString("senderId");
            String senderName = json.optString("senderName");
            String content = json.optString("content");
            String messageType = json.optString("messageType", "TEXT");
            String filePath = json.optString("filePath", null);
            long timestamp = json.optLong("timestamp", System.currentTimeMillis());
            boolean isGroupMessage = json.optBoolean("isGroupMessage", false);

            // Don't save own messages (they're already saved when sent)
            String currentUserId = userPreferences.getUserId();
            if (senderId != null && senderId.equals(currentUserId)) {
                // Update status to DELIVERED if it's our own message
                if (messageId != null && !messageId.isEmpty()) {
                    messageRepository.updateMessageStatus(messageId, "DELIVERED");
                }
                return;
            }

            // Save received message
            Message message = new Message(chatId, senderId, senderName, content);
            message.setMessageId(messageId != null ? messageId : generateMessageId(senderId));
            message.setMessageType(messageType);
            message.setFilePath(filePath);
            message.setTimestamp(timestamp);
            message.setStatus("DELIVERED");
            message.setGroupMessage(isGroupMessage);

            messageRepository.insertMessage(message);

            Log.d(TAG, "Message saved: " + content);
        } catch (Exception e) {
            Log.e(TAG, "Error handling text message", e);
        }
    }

    private void handlePlainTextMessage(String textMessage) {
        // Backward compatibility: treat as simple text message
        // This will be used when connecting with old version
        String currentUserId = userPreferences.getUserId();
        
        // Try to find the chat ID from connected user
        // For now, we'll need sender info - this is a limitation
        // In proper implementation, sender info should be exchanged first
        
        Log.d(TAG, "Received plain text: " + textMessage);
        // Note: Without sender info, we can't save this properly
        // This is why JSON protocol is important
    }

    private void handleUserInfo(JSONObject json) {
        try {
            String userId = json.optString("userId");
            String name = json.optString("name");
            String mobileNumber = json.optString("mobileNumber");
            String deviceId = json.optString("deviceId");
            String ipAddress = json.optString("ipAddress");

            if (userId != null && !userId.isEmpty()) {
                User user = userRepository.getUserByIdSync(userId);
                if (user == null) {
                    // New user, create it
                    user = new User(userId, name, mobileNumber, deviceId);
                }
                user.setIpAddress(ipAddress);
                user.setOnline(true);
                user.setLastSeen(System.currentTimeMillis());
                
                userRepository.updateUser(user);
                Log.d(TAG, "User info updated: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling user info", e);
        }
    }

    /**
     * Create JSON message for sending
     */
    public static String createMessageJson(String chatId, String senderId, String senderName, 
                                          String content, String messageType, boolean isGroupMessage) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "MESSAGE");
            json.put("messageId", generateMessageId(senderId));
            json.put("chatId", chatId);
            json.put("senderId", senderId);
            json.put("senderName", senderName);
            json.put("content", content);
            json.put("messageType", messageType != null ? messageType : "TEXT");
            json.put("timestamp", System.currentTimeMillis());
            json.put("isGroupMessage", isGroupMessage);
            return json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error creating message JSON", e);
            return content; // Fallback to plain text
        }
    }

    /**
     * Create user info JSON for exchange
     */
    public static String createUserInfoJson(String userId, String name, String mobileNumber, 
                                           String deviceId, String ipAddress) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "USER_INFO");
            json.put("userId", userId);
            json.put("name", name);
            json.put("mobileNumber", mobileNumber);
            json.put("deviceId", deviceId);
            json.put("ipAddress", ipAddress);
            return json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error creating user info JSON", e);
            return "";
        }
    }

    private static String generateMessageId(String senderId) {
        return senderId + "_" + System.currentTimeMillis();
    }
}
