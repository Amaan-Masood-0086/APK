package com.example.p.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.p.database.Group;
import com.example.p.database.Message;
import com.example.p.database.User;
import com.example.p.repository.GroupRepository;
import com.example.p.repository.MessageRepository;
import com.example.p.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private GroupRepository groupRepository;
    private LiveData<List<User>> allUsers;
    private LiveData<List<Group>> userGroups;

    public ChatListViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
        messageRepository = new MessageRepository(application);
        groupRepository = new GroupRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<List<Group>> getUserGroups(String userId) {
        userGroups = groupRepository.getGroupsByUserId(userId);
        return userGroups;
    }

    public Message getLastMessage(String chatId) {
        return messageRepository.getLastMessage(chatId);
    }

    public List<ChatListItem> getChatListItems(String currentUserId) {
        List<ChatListItem> items = new ArrayList<>();
        
        // Add individual chats
        List<User> users = allUsers.getValue();
        if (users != null) {
            for (User user : users) {
                if (!user.getUserId().equals(currentUserId)) {
                    Message lastMsg = getLastMessage(user.getUserId());
                    items.add(new ChatListItem(user, lastMsg, false));
                }
            }
        }

        // Add group chats
        List<Group> groups = userGroups != null ? userGroups.getValue() : null;
        if (groups != null) {
            for (Group group : groups) {
                Message lastMsg = getLastMessage(group.getGroupId());
                items.add(new ChatListItem(group, lastMsg, true));
            }
        }

        // Sort by last message timestamp
        items.sort((a, b) -> {
            long timeA = a.getLastMessageTime();
            long timeB = b.getLastMessageTime();
            return Long.compare(timeB, timeA); // Descending order
        });

        return items;
    }

    public static class ChatListItem {
        private User user;
        private Group group;
        private Message lastMessage;
        private boolean isGroup;

        public ChatListItem(User user, Message lastMessage, boolean isGroup) {
            this.user = user;
            this.lastMessage = lastMessage;
            this.isGroup = isGroup;
        }

        public ChatListItem(Group group, Message lastMessage, boolean isGroup) {
            this.group = group;
            this.lastMessage = lastMessage;
            this.isGroup = isGroup;
        }

        public String getTitle() {
            return isGroup ? group.getGroupName() : user.getName();
        }

        public String getSubtitle() {
            if (lastMessage == null) return "No messages";
            return lastMessage.getContent();
        }

        public long getLastMessageTime() {
            return lastMessage != null ? lastMessage.getTimestamp() : 0;
        }

        public String getChatId() {
            return isGroup ? group.getGroupId() : user.getUserId();
        }

        public boolean isGroup() {
            return isGroup;
        }
    }
}
