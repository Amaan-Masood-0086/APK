package com.example.p.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserById(String userId);

    @Query("SELECT * FROM users WHERE userId = :userId")
    LiveData<User> getUserByIdLive(String userId);

    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users WHERE isOnline = 1")
    LiveData<List<User>> getOnlineUsers();

    @Query("UPDATE users SET isOnline = :isOnline, lastSeen = :lastSeen WHERE userId = :userId")
    void updateUserStatus(String userId, boolean isOnline, long lastSeen);

    @Query("UPDATE users SET ipAddress = :ipAddress WHERE userId = :userId")
    void updateUserIpAddress(String userId, String ipAddress);

    @Query("DELETE FROM users WHERE userId = :userId")
    void deleteUser(String userId);
}
