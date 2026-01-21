package com.example.p.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.p.database.Message;
import com.example.p.repository.MessageRepository;
import com.example.p.utils.UserPreferences;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private MessageRepository messageRepository;
    private UserPreferences userPreferences;
    private LiveData<List<Message>> messages;
    private String currentChatId;

    public ChatViewModel(Application application) {
        super(application);
        messageRepository = new MessageRepository(application);
        userPreferences = new UserPreferences(application);
    }

    public void setChatId(String chatId) {
        this.currentChatId = chatId;
        messages = messageRepository.getMessagesByChatId(chatId);
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void sendMessage(String content, String messageType) {
        String senderId = userPreferences.getUserId();
        String senderName = userPreferences.getUserName();
        
        Message message = new Message(currentChatId, senderId, senderName, content);
        message.setMessageType(messageType);
        message.setStatus("PENDING");
        
        messageRepository.insertMessage(message);
    }

    public void sendFileMessage(String content, String filePath, String messageType) {
        String senderId = userPreferences.getUserId();
        String senderName = userPreferences.getUserName();
        
        Message message = new Message(currentChatId, senderId, senderName, content);
        message.setMessageType(messageType);
        message.setFilePath(filePath);
        message.setStatus("PENDING");
        
        messageRepository.insertMessage(message);
    }

    public void updateMessageStatus(String messageId, String status) {
        messageRepository.updateMessageStatus(messageId, status);
    }

    public List<Message> getPendingMessages() {
        return messageRepository.getPendingMessages(currentChatId);
    }

    public String getCurrentChatId() {
        return currentChatId;
    }
}
