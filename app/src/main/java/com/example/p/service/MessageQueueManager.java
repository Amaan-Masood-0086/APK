package com.example.p.service;

import android.content.Context;
import android.util.Log;

import com.example.p.database.Message;
import com.example.p.repository.MessageRepository;
import com.example.p.utils.UserPreferences;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages message queue and retry logic
 */
public class MessageQueueManager {
    private static final String TAG = "MessageQueueManager";
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long INITIAL_RETRY_DELAY = 1000; // 1 second
    private static final long MAX_RETRY_DELAY = 60000; // 60 seconds

    private MessageRepository messageRepository;
    private UserPreferences userPreferences;
    private ScheduledExecutorService scheduler;
    private ExecutorService executorService;
    private boolean isRunning = false;
    private MessageSender messageSender;

    public interface MessageSender {
        boolean sendMessage(Message message);
    }

    public MessageQueueManager(Context context, MessageSender sender) {
        android.app.Application app = (android.app.Application) context.getApplicationContext();
        this.messageRepository = new MessageRepository(app);
        this.userPreferences = new UserPreferences(context);
        this.messageSender = sender;
        this.executorService = Executors.newFixedThreadPool(2);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Start processing message queue
     */
    public void start() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        Log.d(TAG, "Message queue manager started");

        // Process queue every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning) {
                processPendingMessages();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Stop processing queue
     */
    public void stop() {
        isRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
        Log.d(TAG, "Message queue manager stopped");
    }

    /**
     * Process all pending messages
     */
    private void processPendingMessages() {
        executorService.execute(() -> {
            try {
                List<Message> pendingMessages = messageRepository.getAllPendingMessages();
                
                if (pendingMessages == null || pendingMessages.isEmpty()) {
                    return;
                }

                Log.d(TAG, "Processing " + pendingMessages.size() + " pending messages");

                for (Message message : pendingMessages) {
                    if (!isRunning) {
                        break;
                    }

                    // Check retry count (stored in message content or use separate field)
                    // For now, we'll use a simple approach
                    retryMessage(message);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error processing pending messages", e);
            }
        });
    }

    /**
     * Retry sending a message
     */
    private void retryMessage(Message message) {
        executorService.execute(() -> {
            try {
                // Try to send message
                boolean sent = messageSender.sendMessage(message);

                if (sent) {
                    // Update status to SENT
                    messageRepository.updateMessageStatus(message.getMessageId(), "SENT");
                    Log.d(TAG, "Message sent: " + message.getMessageId());
                } else {
                    // Mark as failed after max retries
                    // For now, keep as PENDING for next retry cycle
                    Log.w(TAG, "Failed to send message: " + message.getMessageId());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error retrying message", e);
            }
        });
    }

    /**
     * Add message to queue
     */
    public void queueMessage(Message message) {
        executorService.execute(() -> {
            message.setStatus("PENDING");
            messageRepository.insertMessage(message);
            Log.d(TAG, "Message queued: " + message.getMessageId());
        });
    }

    /**
     * Mark message as delivered
     */
    public void markDelivered(String messageId) {
        executorService.execute(() -> {
            messageRepository.updateMessageStatus(messageId, "DELIVERED");
            Log.d(TAG, "Message delivered: " + messageId);
        });
    }

    /**
     * Mark message as failed
     */
    public void markFailed(String messageId) {
        executorService.execute(() -> {
            messageRepository.updateMessageStatus(messageId, "FAILED");
            Log.d(TAG, "Message failed: " + messageId);
        });
    }

    /**
     * Get pending message count
     */
    public int getPendingCount() {
        List<Message> pending = messageRepository.getAllPendingMessages();
        return pending != null ? pending.size() : 0;
    }
}
