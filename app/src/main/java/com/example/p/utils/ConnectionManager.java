package com.example.p.utils;

import android.content.Context;
import android.util.Log;

import com.example.p.SocketClient;
import com.example.p.SocketServer;
import com.example.p.database.User;
import com.example.p.repository.UserRepository;

/**
 * Manages socket connections and user discovery
 */
public class ConnectionManager {
    private static final String TAG = "ConnectionManager";
    private Context context;
    private UserRepository userRepository;
    private UserPreferences userPreferences;
    private MessageHandler messageHandler;

    public ConnectionManager(Context context) {
        this.context = context.getApplicationContext();
        this.userRepository = new UserRepository((android.app.Application) this.context);
        this.userPreferences = new UserPreferences(context);
        this.messageHandler = new MessageHandler(context);
    }

    /**
     * Setup socket listeners for receiving messages
     */
    public void setupSocketListeners() {
        // Setup server listener
        SocketServer.setOnMessageReceivedListener(message -> {
            handleIncomingMessage(message);
        });

        // Setup client listener
        SocketClient.setOnMessageReceivedListener(message -> {
            handleIncomingMessage(message);
        });
    }

    private void handleIncomingMessage(String message) {
        // Handle user info exchange
        if (message.startsWith("{")) {
            // JSON message
            messageHandler.handleIncomingMessage(message);
        } else {
            // Plain text (backward compatibility)
            messageHandler.handleIncomingMessage(message);
        }
    }

    /**
     * Send user info when connection is established
     */
    public void sendUserInfoOnConnection() {
        String userId = userPreferences.getUserId();
        String name = userPreferences.getUserName();
        String mobileNumber = userPreferences.getMobileNumber();
        String deviceId = userPreferences.getDeviceId();
        String ipAddress = getLocalIPAddress();

        if (userId != null && name != null) {
            String userInfoJson = MessageHandler.createUserInfoJson(
                userId, name, mobileNumber, deviceId, ipAddress
            );

            if (SocketServer.isConnected()) {
                SocketServer.sendMessage(userInfoJson);
            } else if (SocketClient.isConnected()) {
                SocketClient.sendMessage(userInfoJson);
            }
        }
    }

    /**
     * Update user status when going online/offline
     */
    public void updateUserStatus(boolean isOnline) {
        String userId = userPreferences.getUserId();
        if (userId != null) {
            userRepository.updateUserStatus(userId, isOnline);
        }
    }

    private String getLocalIPAddress() {
        try {
            java.util.List<java.net.NetworkInterface> interfaces = 
                java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces());
            for (java.net.NetworkInterface intf : interfaces) {
                java.util.List<java.net.InetAddress> addrs = 
                    java.util.Collections.list(intf.getInetAddresses());
                for (java.net.InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr != null && sAddr.indexOf(':') < 0) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error getting IP address", ex);
        }
        return null;
    }
}
