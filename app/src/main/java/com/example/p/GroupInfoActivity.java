package com.example.p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.Group;
import com.example.p.database.GroupMember;
import com.example.p.database.User;
import com.example.p.repository.UserRepository;
import com.example.p.utils.UserPreferences;
import com.example.p.viewmodel.GroupViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupInfoActivity extends AppCompatActivity implements GroupMemberAdapter.OnMemberActionListener {
    public static final String EXTRA_GROUP_ID = "GROUP_ID";
    
    private ImageButton btnBack;
    private TextView tvGroupAvatar, tvGroupName, tvGroupDescription, tvMemberCount;
    private CardView cardAdminSettings;
    private SwitchCompat switchAdminOnly;
    private Button btnAddMember, btnLeaveGroup, btnDeleteGroup;
    private RecyclerView rvMembers;
    
    private GroupViewModel groupViewModel;
    private GroupMemberAdapter memberAdapter;
    private UserPreferences userPreferences;
    private UserRepository userRepository;
    
    private String groupId;
    private String currentUserId;
    private boolean isCurrentUserAdmin = false;
    private boolean isCurrentUserCreator = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        if (groupId == null) {
            finish();
            return;
        }

        initViews();
        setupViewModels();
        setupRecyclerView();
        setupClickListeners();
        loadGroupData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvGroupAvatar = findViewById(R.id.tvGroupAvatar);
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        cardAdminSettings = findViewById(R.id.cardAdminSettings);
        switchAdminOnly = findViewById(R.id.switchAdminOnly);
        btnAddMember = findViewById(R.id.btnAddMember);
        btnLeaveGroup = findViewById(R.id.btnLeaveGroup);
        btnDeleteGroup = findViewById(R.id.btnDeleteGroup);
        rvMembers = findViewById(R.id.rvMembers);
        
        userPreferences = new UserPreferences(this);
        currentUserId = userPreferences.getUserId();
    }

    private void setupViewModels() {
        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        userRepository = new UserRepository(getApplication());
    }

    private void setupRecyclerView() {
        memberAdapter = new GroupMemberAdapter(currentUserId, this);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);
    }

    private void loadGroupData() {
        // Load group info
        groupViewModel.getGroup(groupId).observe(this, group -> {
            if (group != null) {
                displayGroupInfo(group);
            }
        });

        // Load members
        groupViewModel.getGroupMembers(groupId).observe(this, members -> {
            if (members != null) {
                displayMembers(members);
            }
        });
    }

    private void displayGroupInfo(Group group) {
        String name = group.getGroupName();
        tvGroupAvatar.setText(name != null && !name.isEmpty() ? 
                String.valueOf(name.charAt(0)).toUpperCase() : "G");
        tvGroupName.setText(name);
        
        String description = group.getDescription();
        if (description != null && !description.isEmpty()) {
            tvGroupDescription.setText(description);
            tvGroupDescription.setVisibility(View.VISIBLE);
        } else {
            tvGroupDescription.setVisibility(View.GONE);
        }
        
        // Check if current user is creator
        isCurrentUserCreator = currentUserId.equals(group.getAdminId());
        
        // Show delete button only for creator
        btnDeleteGroup.setVisibility(isCurrentUserCreator ? View.VISIBLE : View.GONE);
        
        // Set admin only switch state
        switchAdminOnly.setChecked(group.isAdminOnlyMessages());
    }

    private void displayMembers(List<GroupMember> members) {
        tvMemberCount.setText(members.size() + " members");
        
        // Check if current user is admin
        for (GroupMember member : members) {
            if (member.getUserId().equals(currentUserId)) {
                isCurrentUserAdmin = member.isAdmin();
                break;
            }
        }
        
        // Show admin controls if user is admin
        cardAdminSettings.setVisibility(isCurrentUserAdmin ? View.VISIBLE : View.GONE);
        btnAddMember.setVisibility(isCurrentUserAdmin ? View.VISIBLE : View.GONE);
        
        memberAdapter.setCurrentUserAdmin(isCurrentUserAdmin);
        memberAdapter.setMembers(members);
        
        // Load user details for members
        loadUserDetails(members);
    }

    private void loadUserDetails(List<GroupMember> members) {
        Map<String, User> userMap = new HashMap<>();
        
        userRepository.getAllUsers().observe(this, users -> {
            if (users != null) {
                for (User user : users) {
                    userMap.put(user.getUserId(), user);
                }
                memberAdapter.setUserMap(userMap);
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddMember.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMemberActivity.class);
            intent.putExtra(AddMemberActivity.EXTRA_GROUP_ID, groupId);
            startActivity(intent);
        });
        
        switchAdminOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isCurrentUserAdmin) {
                groupViewModel.setAdminOnlyMessages(groupId, isChecked);
            }
        });
        
        btnLeaveGroup.setOnClickListener(v -> showLeaveGroupDialog());
        
        btnDeleteGroup.setOnClickListener(v -> showDeleteGroupDialog());
    }

    private void showLeaveGroupDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.leave_group)
                .setMessage(R.string.confirm_leave_group)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    groupViewModel.removeMemberFromGroup(groupId, currentUserId);
                    Toast.makeText(this, "Left group", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showDeleteGroupDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_group)
                .setMessage(R.string.confirm_delete_group)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    // Delete group from repository
                    groupViewModel.deleteGroup(groupId);
                    Toast.makeText(this, "Group deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onMakeAdmin(GroupMember member) {
        groupViewModel.makeAdmin(groupId, member.getUserId());
    }

    @Override
    public void onRemoveAdmin(GroupMember member) {
        groupViewModel.removeAdmin(groupId, member.getUserId());
    }

    @Override
    public void onRemoveMember(GroupMember member) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_member)
                .setMessage("Remove this member from the group?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    groupViewModel.removeMemberFromGroup(groupId, member.getUserId());
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
