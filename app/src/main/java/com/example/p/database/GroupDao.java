package com.example.p.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(Group group);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(List<Group> groups);

    @Update
    void updateGroup(Group group);

    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    Group getGroupById(String groupId);

    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    LiveData<Group> getGroupByIdLive(String groupId);

    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    LiveData<List<Group>> getAllGroups();

    @Query("SELECT * FROM groups WHERE groupId IN (SELECT DISTINCT groupId FROM group_members WHERE userId = :userId)")
    LiveData<List<Group>> getGroupsByUserId(String userId);

    @Query("UPDATE groups SET adminOnlyMessages = :adminOnly WHERE groupId = :groupId")
    void updateAdminOnlyMessages(String groupId, boolean adminOnly);

    @Query("DELETE FROM groups WHERE groupId = :groupId")
    void deleteGroup(String groupId);
}
