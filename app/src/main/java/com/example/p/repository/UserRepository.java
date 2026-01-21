package com.example.p.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.p.database.LinkUpDatabase;
import com.example.p.database.User;
import com.example.p.database.UserDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        LinkUpDatabase database = LinkUpDatabase.getDatabase(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getUserById(String userId) {
        return userDao.getUserByIdLive(userId);
    }

    public LiveData<List<User>> getOnlineUsers() {
        return userDao.getOnlineUsers();
    }

    public void insertUser(User user) {
        executorService.execute(() -> userDao.insertUser(user));
    }

    public void insertUsers(List<User> users) {
        executorService.execute(() -> userDao.insertUsers(users));
    }

    public void updateUser(User user) {
        executorService.execute(() -> userDao.updateUser(user));
    }

    public void updateUserStatus(String userId, boolean isOnline) {
        executorService.execute(() -> 
            userDao.updateUserStatus(userId, isOnline, System.currentTimeMillis())
        );
    }

    public void updateUserIpAddress(String userId, String ipAddress) {
        executorService.execute(() -> userDao.updateUserIpAddress(userId, ipAddress));
    }

    public void deleteUser(String userId) {
        executorService.execute(() -> userDao.deleteUser(userId));
    }

    public User getUserByIdSync(String userId) {
        return userDao.getUserById(userId);
    }
}
