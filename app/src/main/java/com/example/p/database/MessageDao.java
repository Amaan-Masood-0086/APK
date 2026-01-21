package com.example.p.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(Message message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<Message> messages);

    @Update
    void updateMessage(Message message);

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    LiveData<List<Message>> getMessagesByChatId(String chatId);

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    List<Message> getMessagesByChatIdSync(String chatId);

    @Query("SELECT * FROM messages WHERE status = 'PENDING' AND chatId = :chatId")
    List<Message> getPendingMessages(String chatId);

    @Query("SELECT * FROM messages WHERE status = 'PENDING'")
    List<Message> getAllPendingMessages();

    @Query("UPDATE messages SET status = :status WHERE messageId = :messageId")
    void updateMessageStatus(String messageId, String status);

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 1")
    Message getLastMessage(String chatId);

    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId AND status = 'PENDING'")
    int getPendingMessageCount(String chatId);

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    void deleteChatMessages(String chatId);

    @Query("DELETE FROM messages WHERE id = :id")
    void deleteMessage(long id);
}
