package com.example.p.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.p.database.Group;
import com.example.p.database.GroupDao;
import com.example.p.database.GroupMember;
import com.example.p.database.GroupMemberDao;
import com.example.p.database.LinkUpDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupRepository {
    private GroupDao groupDao;
    private GroupMemberDao groupMemberDao;
    private LiveData<List<Group>> allGroups;
    private ExecutorService executorService;

    public GroupRepository(Application application) {
        LinkUpDatabase database = LinkUpDatabase.getDatabase(application);
        groupDao = database.groupDao();
        groupMemberDao = database.groupMemberDao();
        allGroups = groupDao.getAllGroups();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Group>> getAllGroups() {
        return allGroups;
    }

    public LiveData<Group> getGroupById(String groupId) {
        return groupDao.getGroupByIdLive(groupId);
    }

    public LiveData<List<Group>> getGroupsByUserId(String userId) {
        return groupDao.getGroupsByUserId(userId);
    }

    public Group getGroupByIdSync(String groupId) {
        return groupDao.getGroupById(groupId);
    }

    public void insertGroup(Group group) {
        executorService.execute(() -> groupDao.insertGroup(group));
    }

    public void updateGroup(Group group) {
        executorService.execute(() -> groupDao.updateGroup(group));
    }

    public void updateAdminOnlyMessages(String groupId, boolean adminOnly) {
        executorService.execute(() -> groupDao.updateAdminOnlyMessages(groupId, adminOnly));
    }

    public void deleteGroup(String groupId) {
        executorService.execute(() -> groupDao.deleteGroup(groupId));
    }

    // Group Member operations
    public LiveData<List<GroupMember>> getGroupMembers(String groupId) {
        return groupMemberDao.getGroupMembers(groupId);
    }

    public List<GroupMember> getGroupMembersSync(String groupId) {
        return groupMemberDao.getGroupMembersSync(groupId);
    }

    public void insertGroupMember(GroupMember member) {
        executorService.execute(() -> groupMemberDao.insertGroupMember(member));
    }

    public void insertGroupMembers(List<GroupMember> members) {
        executorService.execute(() -> groupMemberDao.insertGroupMembers(members));
    }

    public void updateMemberAdminStatus(String groupId, String userId, boolean isAdmin) {
        executorService.execute(() -> 
            groupMemberDao.updateMemberAdminStatus(groupId, userId, isAdmin)
        );
    }

    public void removeGroupMember(String groupId, String userId) {
        executorService.execute(() -> groupMemberDao.removeGroupMember(groupId, userId));
    }

    public List<GroupMember> getGroupAdmins(String groupId) {
        return groupMemberDao.getGroupAdmins(groupId);
    }

    public boolean isUserAdmin(String groupId, String userId) {
        GroupMember member = groupMemberDao.getGroupMember(groupId, userId);
        return member != null && member.isAdmin();
    }
}
