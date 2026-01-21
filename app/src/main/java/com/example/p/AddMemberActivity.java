package com.example.p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.GroupMember;
import com.example.p.database.User;
import com.example.p.utils.UserPreferences;
import com.example.p.viewmodel.ChatListViewModel;
import com.example.p.viewmodel.GroupViewModel;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddMemberActivity extends AppCompatActivity {
    public static final String EXTRA_GROUP_ID = "GROUP_ID";
    
    private ImageButton btnBack;
    private RecyclerView rvAvailableMembers;
    private TextView tvNoMembers;
    private Button btnScanQR;
    
    private GroupViewModel groupViewModel;
    private ChatListViewModel chatListViewModel;
    private AddMemberAdapter memberAdapter;
    private UserPreferences userPreferences;
    
    private String groupId;
    private Set<String> existingMemberIds = new HashSet<>();

    private final ActivityResultLauncher<ScanOptions> qrScanLauncher = registerForActivityResult(
            new ScanContract(), 
            result -> {
                if (result.getContents() != null) {
                    handleQrResult(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        if (groupId == null) {
            finish();
            return;
        }

        initViews();
        setupViewModels();
        setupRecyclerView();
        setupClickListeners();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvAvailableMembers = findViewById(R.id.rvAvailableMembers);
        tvNoMembers = findViewById(R.id.tvNoMembers);
        btnScanQR = findViewById(R.id.btnScanQR);
        
        userPreferences = new UserPreferences(this);
    }

    private void setupViewModels() {
        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        chatListViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);
    }

    private void setupRecyclerView() {
        memberAdapter = new AddMemberAdapter(userId -> {
            // Add member to group
            groupViewModel.addMemberToGroup(groupId, userId);
            Toast.makeText(this, R.string.member_added, Toast.LENGTH_SHORT).show();
            existingMemberIds.add(userId);
            loadAvailableMembers();
        });
        rvAvailableMembers.setLayoutManager(new LinearLayoutManager(this));
        rvAvailableMembers.setAdapter(memberAdapter);
    }

    private void loadData() {
        // First load existing members
        groupViewModel.getGroupMembers(groupId).observe(this, members -> {
            existingMemberIds.clear();
            if (members != null) {
                for (GroupMember member : members) {
                    existingMemberIds.add(member.getUserId());
                }
            }
            loadAvailableMembers();
        });
    }

    private void loadAvailableMembers() {
        String currentUserId = userPreferences.getUserId();
        
        chatListViewModel.getAllUsers().observe(this, users -> {
            if (users == null) {
                showNoMembers();
                return;
            }
            
            // Filter out current user and existing members
            List<User> availableUsers = new ArrayList<>();
            for (User user : users) {
                if (!user.getUserId().equals(currentUserId) && 
                    !existingMemberIds.contains(user.getUserId())) {
                    availableUsers.add(user);
                }
            }
            
            if (availableUsers.isEmpty()) {
                showNoMembers();
            } else {
                tvNoMembers.setVisibility(View.GONE);
                rvAvailableMembers.setVisibility(View.VISIBLE);
                memberAdapter.setUsers(availableUsers);
            }
        });
    }

    private void showNoMembers() {
        tvNoMembers.setVisibility(View.VISIBLE);
        rvAvailableMembers.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnScanQR.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Scan member's QR code");
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(false);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureActivityPortrait.class);
            qrScanLauncher.launch(options);
        });
    }

    private void handleQrResult(String contents) {
        // QR code should contain user info in format: userId|name|mobile|deviceId
        try {
            String[] parts = contents.split("\\|");
            if (parts.length >= 2) {
                String userId = parts[0];
                
                // Check if already a member
                if (existingMemberIds.contains(userId)) {
                    Toast.makeText(this, R.string.member_already_exists, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Add member
                groupViewModel.addMemberToGroup(groupId, userId);
                Toast.makeText(this, R.string.member_added, Toast.LENGTH_SHORT).show();
                existingMemberIds.add(userId);
                loadAvailableMembers();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
