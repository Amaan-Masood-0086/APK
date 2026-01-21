package com.example.p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p.database.Group;
import com.example.p.database.Message;
import com.example.p.database.User;
import com.example.p.repository.GroupRepository;
import com.example.p.repository.MessageRepository;
import com.example.p.repository.UserRepository;
import com.example.p.utils.UserPreferences;
import com.example.p.viewmodel.ChatViewModel;
import com.example.p.viewmodel.GroupViewModel;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnBack, btnInfo;
    private TextView tvChatAvatar, tvChatName, tvChatStatus;
    private LinearLayout chatInfoContainer;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;

    private MessageAdapter adapter;
    private ChatViewModel viewModel;
    private GroupViewModel groupViewModel;
    private UserPreferences userPreferences;
    private UserRepository userRepository;
    private String chatId;
    private boolean isGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Header views
        btnBack = findViewById(R.id.btnBack);
        btnInfo = findViewById(R.id.btnInfo);
        tvChatAvatar = findViewById(R.id.tvChatAvatar);
        tvChatName = findViewById(R.id.tvChatName);
        tvChatStatus = findViewById(R.id.tvChatStatus);
        chatInfoContainer = findViewById(R.id.chatInfoContainer);
        
        // Message views
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Get chat ID from intent
        chatId = getIntent().getStringExtra("CHAT_ID");
        isGroupChat = getIntent().getBooleanExtra("IS_GROUP", false);

        if (chatId == null) {
            finish();
            return;
        }

        userPreferences = new UserPreferences(this);
        userRepository = new UserRepository(getApplication());
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.setChatId(chatId);

        // Setup header
        setupHeader();

        String currentUserId = userPreferences.getUserId();
        adapter = new MessageAdapter(currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        // Observe messages from database
        viewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(messages);
            if (messages != null && !messages.isEmpty()) {
                rvMessages.scrollToPosition(messages.size() - 1);
            }
        });

        // Setup socket listeners for real-time messaging
        setupSocketListeners();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // ✅ SEND MESSAGE
        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                viewModel.sendMessage(msg, "TEXT");
                
                // Send via socket (for real-time delivery)
                sendMessageViaSocket(msg);
                
                etMessage.setText("");
            }
        });
    }

    private void setupHeader() {
        if (isGroupChat) {
            // Group chat - show info button
            btnInfo.setVisibility(View.VISIBLE);
            
            groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
            groupViewModel.getGroup(chatId).observe(this, group -> {
                if (group != null) {
                    String name = group.getGroupName();
                    tvChatName.setText(name);
                    tvChatAvatar.setText(name != null && !name.isEmpty() ? 
                            String.valueOf(name.charAt(0)).toUpperCase() : "G");
                }
            });
            
            // Show member count
            groupViewModel.getGroupMembers(chatId).observe(this, members -> {
                if (members != null) {
                    tvChatStatus.setText(members.size() + " members");
                    tvChatStatus.setVisibility(View.VISIBLE);
                }
            });
            
            // Info button click
            btnInfo.setOnClickListener(v -> {
                Intent intent = new Intent(this, GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.EXTRA_GROUP_ID, chatId);
                startActivity(intent);
            });
            
            // Header click also opens info
            chatInfoContainer.setOnClickListener(v -> {
                Intent intent = new Intent(this, GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.EXTRA_GROUP_ID, chatId);
                startActivity(intent);
            });
            
        } else {
            // Individual chat
            btnInfo.setVisibility(View.GONE);
            
            userRepository.getUserById(chatId).observe(this, user -> {
                if (user != null) {
                    String name = user.getName();
                    tvChatName.setText(name);
                    tvChatAvatar.setText(name != null && !name.isEmpty() ? 
                            String.valueOf(name.charAt(0)).toUpperCase() : "?");
                    
                    // Show online status
                    if (user.isOnline()) {
                        tvChatStatus.setText("Online");
                        tvChatStatus.setVisibility(View.VISIBLE);
                    } else {
                        tvChatStatus.setVisibility(View.GONE);
                    }
                } else {
                    tvChatName.setText("Chat");
                    tvChatAvatar.setText("?");
                }
            });
        }
    }

    private void setupSocketListeners() {
        // Setup listener to save incoming messages to database
        com.example.p.utils.MessageHandler messageHandler = 
            new com.example.p.utils.MessageHandler(this);
        
        if (SocketServer.isConnected()) {
            SocketServer.setOnMessageReceivedListener(msg -> {
                messageHandler.handleIncomingMessage(msg);
            });
        } else if (SocketClient.isConnected()) {
            SocketClient.setOnMessageReceivedListener(msg -> {
                messageHandler.handleIncomingMessage(msg);
            });
        }
    }

    private void sendMessageViaSocket(String message) {
        // Create JSON message with proper format
        String senderId = userPreferences.getUserId();
        String senderName = userPreferences.getUserName();
        String messageJson = com.example.p.utils.MessageHandler.createMessageJson(
            chatId, senderId, senderName, message, "TEXT", isGroupChat
        );
        
        if (SocketServer.isConnected()) {
            SocketServer.sendMessage(messageJson);
        } else if (SocketClient.isConnected()) {
            SocketClient.sendMessage(messageJson);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Socket cleanup handled elsewhere
    }
}
