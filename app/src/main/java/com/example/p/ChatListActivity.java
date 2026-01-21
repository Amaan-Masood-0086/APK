package com.example.p;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.utils.UserPreferences;
import com.example.p.viewmodel.ChatListViewModel;

import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView rvChatList;
    private Button btnNewChat, btnCreateGroup;
    private ChatListViewModel viewModel;
    private UserPreferences userPreferences;
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        userPreferences = new UserPreferences(this);
        
        // Check if user is registered
        if (!userPreferences.isUserRegistered()) {
            startActivity(new Intent(this, UserSetupActivity.class));
            finish();
            return;
        }

        rvChatList = findViewById(R.id.rvChatList);
        btnNewChat = findViewById(R.id.btnNewChat);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);

        viewModel = new ViewModelProvider(this).get(ChatListViewModel.class);
        String currentUserId = userPreferences.getUserId();

        adapter = new ChatListAdapter((chatId, isGroup) -> {
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("CHAT_ID", chatId);
            intent.putExtra("IS_GROUP", isGroup);
            startActivity(intent);
        });

        rvChatList.setLayoutManager(new LinearLayoutManager(this));
        rvChatList.setAdapter(adapter);

        // Observe users and groups
        viewModel.getAllUsers().observe(this, users -> updateChatList(currentUserId));
        viewModel.getUserGroups(currentUserId).observe(this, groups -> updateChatList(currentUserId));

        btnNewChat.setOnClickListener(v -> {
            // Navigate to QR Connect to add new contact
            Intent intent = new Intent(this, QrConnectActivity.class);
            startActivity(intent);
        });

        btnCreateGroup.setOnClickListener(v -> {
            // Navigate to Create Group screen
            startActivity(new Intent(this, CreateGroupActivity.class));
        });
    }

    private void updateChatList(String currentUserId) {
        List<ChatListViewModel.ChatListItem> items = viewModel.getChatListItems(currentUserId);
        adapter.setChatList(items);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentUserId = userPreferences.getUserId();
        if (currentUserId != null) {
            updateChatList(currentUserId);
        }
    }
}
