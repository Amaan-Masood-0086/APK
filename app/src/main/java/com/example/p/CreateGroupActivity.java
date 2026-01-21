package com.example.p;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.example.p.utils.UserPreferences;
import com.example.p.viewmodel.GroupViewModel;
import com.example.p.viewmodel.ChatListViewModel;

import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextInputEditText etGroupName, etGroupDescription;
    private RecyclerView rvMembers;
    private TextView tvNoMembers;
    private Button btnCreateGroup;
    
    private GroupViewModel groupViewModel;
    private ChatListViewModel chatListViewModel;
    private MemberSelectAdapter memberAdapter;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initViews();
        setupViewModels();
        setupRecyclerView();
        setupClickListeners();
        loadMembers();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etGroupName = findViewById(R.id.etGroupName);
        etGroupDescription = findViewById(R.id.etGroupDescription);
        rvMembers = findViewById(R.id.rvMembers);
        tvNoMembers = findViewById(R.id.tvNoMembers);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        
        userPreferences = new UserPreferences(this);
    }

    private void setupViewModels() {
        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        chatListViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);
    }

    private void setupRecyclerView() {
        memberAdapter = new MemberSelectAdapter();
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);
    }

    private void loadMembers() {
        String currentUserId = userPreferences.getUserId();
        
        chatListViewModel.getAllUsers().observe(this, users -> {
            if (users == null || users.isEmpty()) {
                tvNoMembers.setVisibility(View.VISIBLE);
                rvMembers.setVisibility(View.GONE);
            } else {
                tvNoMembers.setVisibility(View.GONE);
                rvMembers.setVisibility(View.VISIBLE);
                
                // Filter out current user
                users.removeIf(user -> user.getUserId().equals(currentUserId));
                memberAdapter.setUsers(users);
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnCreateGroup.setOnClickListener(v -> createGroup());
    }

    private void createGroup() {
        String groupName = etGroupName.getText() != null ? 
                etGroupName.getText().toString().trim() : "";
        String description = etGroupDescription.getText() != null ? 
                etGroupDescription.getText().toString().trim() : "";
        
        // Validate group name
        if (groupName.isEmpty()) {
            etGroupName.setError(getString(R.string.group_name_required));
            etGroupName.requestFocus();
            return;
        }
        
        List<String> selectedMembers = memberAdapter.getSelectedUserIds();
        
        // Create group
        groupViewModel.createGroup(groupName, description, selectedMembers);
        
        Toast.makeText(this, R.string.group_created, Toast.LENGTH_SHORT).show();
        finish();
    }
}
