package com.example.p.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.p.database.LinkUpDatabase;
import com.example.p.database.Message;
import com.example.p.database.MessageDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageRepository {
    private MessageDao messageDao;
    private LiveData<List<Message>> messages;
    private ExecutorService executorService;

    public MessageRepository(Application application) {
        LinkUpDatabase database = LinkUpDatabase.getDatabase(application);
        messageDao = database.messageDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Message>> getMessagesByChatId(String chatId) {
        return messageDao.getMessagesByChatId(chatId);
    }

    public List<Message> getMessagesByChatIdSync(String chatId) {
        return messageDao.getMessagesByChatIdSync(chatId);
    }

    public void insertMessage(Message message) {
        executorService.execute(() -> messageDao.insertMessage(message));
    }

    public void insertMessages(List<Message> messages) {
        executorService.execute(() -> messageDao.insertMessages(messages));
    }

    public void updateMessage(Message message) {
        executorService.execute(() -> messageDao.updateMessage(message));
    }

    public void updateMessageStatus(String messageId, String status) {
        executorService.execute(() -> messageDao.updateMessageStatus(messageId, status));
    }

    public List<Message> getPendingMessages(String chatId) {
        return messageDao.getPendingMessages(chatId);
    }

    public List<Message> getAllPendingMessages() {
        return messageDao.getAllPendingMessages();
    }

    public Message getLastMessage(String chatId) {
        return messageDao.getLastMessage(chatId);
    }

    public void deleteChatMessages(String chatId) {
        executorService.execute(() -> messageDao.deleteChatMessages(chatId));
    }
}
