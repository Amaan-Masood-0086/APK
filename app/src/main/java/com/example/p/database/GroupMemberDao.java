package com.example.p.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupMember(GroupMember member);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupMembers(List<GroupMember> members);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    LiveData<List<GroupMember>> getGroupMembers(String groupId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    List<GroupMember> getGroupMembersSync(String groupId);

    @Query("SELECT * FROM group_members WHERE userId = :userId")
    LiveData<List<GroupMember>> getGroupsByUser(String userId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND userId = :userId")
    GroupMember getGroupMember(String groupId, String userId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND isAdmin = 1")
    List<GroupMember> getGroupAdmins(String groupId);

    @Query("UPDATE group_members SET isAdmin = :isAdmin WHERE groupId = :groupId AND userId = :userId")
    void updateMemberAdminStatus(String groupId, String userId, boolean isAdmin);

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND userId = :userId")
    void removeGroupMember(String groupId, String userId);

    @Query("DELETE FROM group_members WHERE groupId = :groupId")
    void deleteAllGroupMembers(String groupId);
}
