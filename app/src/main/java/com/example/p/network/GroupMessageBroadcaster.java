package com.example.p.network;

import android.content.Context;
import android.util.Log;

import com.example.p.database.GroupMember;
import com.example.p.database.Message;
import com.example.p.database.User;
import com.example.p.repository.GroupRepository;
import com.example.p.repository.MessageRepository;
import com.example.p.repository.UserRepository;
import com.example.p.utils.MessageHandler;
import com.example.p.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles broadcasting messages to group members
 */
public class GroupMessageBroadcaster {
    private static final String TAG = "GroupBroadcaster";
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private UserPreferences userPreferences;
    private ExecutorService executorService;

    public GroupMessageBroadcaster(Context context) {
        android.app.Application app = (android.app.Application) context.getApplicationContext();
        this.groupRepository = new GroupRepository(app);
        this.userRepository = new UserRepository(app);
        this.messageRepository = new MessageRepository(app);
        this.userPreferences = new UserPreferences(context);
        this.executorService = Executors.newFixedThreadPool(4);
    }

    /**
     * Broadcast message to all group members
     */
    public void broadcastGroupMessage(String groupId, String senderId, String senderName,
                                     String content, String messageType) {
        executorService.execute(() -> {
            try {
                // Get all group members
                List<GroupMember> members = groupRepository.getGroupMembersSync(groupId);
                if (members == null || members.isEmpty()) {
                    Log.w(TAG, "No members found for group: " + groupId);
                    return;
                }

                // Create message
                String messageId = senderId + "_" + System.currentTimeMillis();
                String messageJson = MessageProtocol.createMessage(
                    messageId, groupId, senderId, senderName, content, messageType, true
                );

                if (messageJson == null) {
                    Log.e(TAG, "Failed to create message JSON");
                    return;
                }

                // Get current user ID to exclude from broadcast
                String currentUserId = userPreferences.getUserId();

                // Collect member IPs for broadcasting
                List<String> memberIds = new ArrayList<>();
                List<String> offlineMemberIds = new ArrayList<>();

                for (GroupMember member : members) {
                    String memberId = member.getUserId();
                    
                    // Skip sender
                    if (memberId.equals(currentUserId)) {
                        continue;
                    }

                    // Get user info
                    User user = userRepository.getUserByIdSync(memberId);
                    if (user != null) {
                        if (user.isOnline() && user.getIpAddress() != null) {
                            memberIds.add(memberId);
                        } else {
                            offlineMemberIds.add(memberId);
                        }
                    }
                }

                // Save message for offline members (they'll receive when online)
                for (String offlineId : offlineMemberIds) {
                    Message offlineMessage = new Message(groupId, senderId, senderName, content);
                    offlineMessage.setMessageId(messageId);
                    offlineMessage.setMessageType(messageType);
                    offlineMessage.setStatus("PENDING");
                    offlineMessage.setGroupMessage(true);
                    messageRepository.insertMessage(offlineMessage);
                }

                // Broadcast to online members via socket
                // Note: This requires mapping userId to socket clientId
                // For now, we'll use the enhanced server's broadcast capability
                if (!memberIds.isEmpty()) {
                    EnhancedSocketServer.broadcastMessage(messageJson);
                    Log.d(TAG, "Broadcasted to " + memberIds.size() + " online members");
                }

                // Update message status for sent messages
                messageRepository.updateMessageStatus(messageId, "SENT");

            } catch (Exception e) {
                Log.e(TAG, "Error broadcasting group message", e);
            }
        });
    }

    /**
     * Send message to specific group members
     */
    public void sendToGroupMembers(String groupId, List<String> targetMemberIds,
                                   String senderId, String senderName, String content,
                                   String messageType) {
        executorService.execute(() -> {
            try {
                String messageId = senderId + "_" + System.currentTimeMillis();
                String messageJson = MessageProtocol.createMessage(
                    messageId, groupId, senderId, senderName, content, messageType, true
                );

                if (messageJson == null) {
                    return;
                }

                // Send to each target member
                for (String memberId : targetMemberIds) {
                    User user = userRepository.getUserByIdSync(memberId);
                    if (user != null && user.isOnline()) {
                        // Send via socket (would need clientId mapping)
                        EnhancedSocketServer.broadcastMessage(messageJson);
                    } else {
                        // Queue for offline member
                        Message offlineMessage = new Message(groupId, senderId, senderName, content);
                        offlineMessage.setMessageId(messageId);
                        offlineMessage.setMessageType(messageType);
                        offlineMessage.setStatus("PENDING");
                        offlineMessage.setGroupMessage(true);
                        messageRepository.insertMessage(offlineMessage);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error sending to group members", e);
            }
        });
    }
}
