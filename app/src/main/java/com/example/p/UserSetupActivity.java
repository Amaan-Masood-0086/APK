package com.example.p;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.p.database.User;
import com.example.p.repository.UserRepository;
import com.example.p.utils.DeviceUtils;
import com.example.p.utils.UserPreferences;

public class UserSetupActivity extends AppCompatActivity {
    private EditText etName, etMobileNumber;
    private Button btnSave;
    private UserRepository userRepository;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user already registered
        userPreferences = new UserPreferences(this);
        if (userPreferences.isUserRegistered()) {
            startActivity(new Intent(this, ChatListActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_user_setup);

        etName = findViewById(R.id.etName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        btnSave = findViewById(R.id.btnSave);

        userRepository = new UserRepository(getApplication());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobileNumber = etMobileNumber.getText().toString().trim();

            if (name.isEmpty() || mobileNumber.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mobileNumber.length() < 10) {
                Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate user ID
            String deviceId = DeviceUtils.getDeviceId(this);
            String userId = DeviceUtils.generateUserId(mobileNumber, deviceId);

            // Save to preferences
            userPreferences.setUserId(userId);
            userPreferences.setUserName(name);
            userPreferences.setMobileNumber(mobileNumber);
            userPreferences.setDeviceId(deviceId);

            // Save to database
            User user = new User(userId, name, mobileNumber, deviceId);
            userRepository.insertUser(user);

            Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
            
            // Navigate to QR Connect or Chat List
            startActivity(new Intent(this, QrConnectActivity.class));
            finish();
        });
    }
}
