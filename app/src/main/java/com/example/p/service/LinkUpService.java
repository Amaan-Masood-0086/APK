package com.example.p.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.p.ChatListActivity;
import com.example.p.R;
import com.example.p.database.Message;
import com.example.p.network.EnhancedSocketServer;
import com.example.p.network.MessageProtocol;
import com.example.p.SocketClient;
import com.example.p.repository.MessageRepository;
import com.example.p.utils.MessageHandler;
import com.example.p.utils.UserPreferences;

/**
 * Foreground service for background message handling
 */
public class LinkUpService extends Service {
    private static final String TAG = "LinkUpService";
    private static final String CHANNEL_ID = "LinkUpServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private MessageRepository messageRepository;
    private MessageQueueManager queueManager;
    private MessageHandler messageHandler;
    private UserPreferences userPreferences;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        messageRepository = new MessageRepository(getApplication());
        messageHandler = new MessageHandler(this);
        userPreferences = new UserPreferences(this);

        // Create notification channel
        createNotificationChannel();

        // Initialize message queue manager
        queueManager = new MessageQueueManager(this, message -> {
            // Try to send via socket
            return sendMessageViaSocket(message);
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        // Start foreground service
        startForeground(NOTIFICATION_ID, createNotification("LinkUp is running"));

        if (!isRunning) {
            isRunning = true;
            startService();
        }

        return START_STICKY; // Restart if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        isRunning = false;
        
        if (queueManager != null) {
            queueManager.stop();
        }
        
        EnhancedSocketServer.closeServer();
        SocketClient.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    /**
     * Start service operations
     */
    private void startService() {
        // Start message queue manager
        if (queueManager != null) {
            queueManager.start();
        }

        // Setup socket listeners
        setupSocketListeners();

        Log.d(TAG, "Service operations started");
    }

    /**
     * Setup socket message listeners
     */
    private void setupSocketListeners() {
        // Enhanced server listener
        EnhancedSocketServer.setMessageListener((clientId, message) -> {
            handleIncomingMessage(message);
        });

        // Client listener (for backward compatibility)
        SocketClient.setOnMessageReceivedListener(message -> {
            handleIncomingMessage(message);
        });
    }

    /**
     * Handle incoming message
     */
    private void handleIncomingMessage(String message) {
        try {
            // Handle via MessageHandler
            messageHandler.handleIncomingMessage(message);

            // Send acknowledgment if required
            if (MessageProtocol.requiresAck(message)) {
                String messageId = MessageProtocol.getMessageId(message);
                if (messageId != null && !messageId.isEmpty()) {
                    String ack = MessageProtocol.createAck(messageId, "DELIVERED");
                    sendAck(ack);
                }
            }

            // Show notification for new messages
            showMessageNotification(message);

        } catch (Exception e) {
            Log.e(TAG, "Error handling incoming message", e);
        }
    }

    /**
     * Send message via socket
     */
    private boolean sendMessageViaSocket(Message message) {
        try {
            String messageJson = MessageProtocol.createMessage(
                message.getMessageId(),
                message.getChatId(),
                message.getSenderId(),
                message.getSenderName(),
                message.getContent(),
                message.getMessageType(),
                message.isGroupMessage()
            );

            if (messageJson == null) {
                return false;
            }

            // Try enhanced server first
            if (EnhancedSocketServer.isRunning()) {
                EnhancedSocketServer.broadcastMessage(messageJson);
                return true;
            }

            // Fallback to old socket client
            if (SocketClient.isConnected()) {
                SocketClient.sendMessage(messageJson);
                return true;
            }

            return false; // No connection available

        } catch (Exception e) {
            Log.e(TAG, "Error sending message via socket", e);
            return false;
        }
    }

    /**
     * Send acknowledgment
     */
    private void sendAck(String ackMessage) {
        if (EnhancedSocketServer.isRunning()) {
            EnhancedSocketServer.broadcastMessage(ackMessage);
        } else if (SocketClient.isConnected()) {
            SocketClient.sendMessage(ackMessage);
        }
    }

    /**
     * Show notification for new message
     */
    private void showMessageNotification(String messageJson) {
        try {
            // Parse message to get content
            org.json.JSONObject json = new org.json.JSONObject(messageJson);
            String content = json.optString("content", "New message");
            String senderName = json.optString("senderName", "Someone");

            NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent intent = new Intent(this, ChatListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(senderName)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

            notificationManager.notify((int) System.currentTimeMillis(), notification);

        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }

    /**
     * Create notification channel (Android 8+)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "LinkUp Service Channel",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notifications for LinkUp messages");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Create foreground service notification
     */
    private Notification createNotification(String text) {
        Intent notificationIntent = new Intent(this, ChatListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LinkUp")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build();
    }
}
