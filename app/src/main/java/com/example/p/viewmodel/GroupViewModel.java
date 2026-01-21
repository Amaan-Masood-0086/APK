package com.example.p.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.p.database.Group;
import com.example.p.database.GroupMember;
import com.example.p.repository.GroupRepository;
import com.example.p.utils.UserPreferences;

import java.util.List;

public class GroupViewModel extends AndroidViewModel {
    private GroupRepository groupRepository;
    private UserPreferences userPreferences;

    public GroupViewModel(Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        userPreferences = new UserPreferences(application);
    }

    public LiveData<Group> getGroup(String groupId) {
        return groupRepository.getGroupById(groupId);
    }

    public LiveData<List<GroupMember>> getGroupMembers(String groupId) {
        return groupRepository.getGroupMembers(groupId);
    }

    public void createGroup(String groupName, String description, List<String> memberIds) {
        String adminId = userPreferences.getUserId();
        String groupId = "group_" + System.currentTimeMillis();
        
        Group group = new Group(groupId, groupName, adminId);
        group.setDescription(description);
        groupRepository.insertGroup(group);

        // Add admin as member
        GroupMember adminMember = new GroupMember(groupId, adminId, true);
        groupRepository.insertGroupMember(adminMember);

        // Add other members
        for (String memberId : memberIds) {
            if (!memberId.equals(adminId)) {
                GroupMember member = new GroupMember(groupId, memberId, false);
                groupRepository.insertGroupMember(member);
            }
        }
    }

    public void addMemberToGroup(String groupId, String userId) {
        GroupMember member = new GroupMember(groupId, userId, false);
        groupRepository.insertGroupMember(member);
    }

    public void removeMemberFromGroup(String groupId, String userId) {
        groupRepository.removeGroupMember(groupId, userId);
    }

    public void makeAdmin(String groupId, String userId) {
        groupRepository.updateMemberAdminStatus(groupId, userId, true);
    }

    public void removeAdmin(String groupId, String userId) {
        groupRepository.updateMemberAdminStatus(groupId, userId, false);
    }

    public void setAdminOnlyMessages(String groupId, boolean adminOnly) {
        groupRepository.updateAdminOnlyMessages(groupId, adminOnly);
    }

    public void deleteGroup(String groupId) {
        groupRepository.deleteGroup(groupId);
    }

    public boolean isUserAdmin(String groupId) {
        String userId = userPreferences.getUserId();
        return groupRepository.isUserAdmin(groupId, userId);
    }

    public boolean canSendMessage(String groupId) {
        Group group = groupRepository.getGroupByIdSync(groupId);
        if (group == null) return false;
        
        if (!group.isAdminOnlyMessages()) {
            return true; // Everyone can send
        }
        
        // Only admins can send
        return isUserAdmin(groupId);
    }
}
