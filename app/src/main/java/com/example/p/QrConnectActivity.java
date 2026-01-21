package com.example.p;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class QrConnectActivity extends AppCompatActivity {

    public static final String IS_SERVER_KEY = "is_server";
    private static final int PORT = 5050;
    private static final String TAG = "QrConnectActivity";

    private ImageView ivQrCode;
    private TextView tvStatus;
    private UserRepository userRepository;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_connect);

        ivQrCode = findViewById(R.id.ivQrCode);
        Button btnGenerateQr = findViewById(R.id.btnGenerateQr);
        Button btnScanQr = findViewById(R.id.btnScanQr);
        tvStatus = findViewById(R.id.tvStatus);

        userRepository = new UserRepository(getApplication());
        userPreferences = new UserPreferences(this);

        btnGenerateQr.setOnClickListener(v -> generateQr());
        btnScanQr.setOnClickListener(v -> scanQr());
    }

    private void generateQr() {
        String ip = getIPAddress();
        if (ip == null) {
            Toast.makeText(this, R.string.error_could_not_get_ip, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(ip, BarcodeFormat.QR_CODE, 400, 400);
            ivQrCode.setImageBitmap(bitmap);
            tvStatus.setText(getString(R.string.status_waiting_for_connection, ip));

            SocketServer.startServer(PORT, () -> runOnUiThread(() -> {
                // Send user info to connected client
                sendUserInfo();
                
                // Navigate to ChatList instead of direct chat
                Intent intent = new Intent(QrConnectActivity.this, ChatListActivity.class);
                startActivity(intent);
                finish();
            }));

        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code", e);
        }
    }

    private void scanQr() {
        ScanOptions options = new ScanOptions();
        options.setPrompt(getString(R.string.prompt_scan_qr));
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String ip = result.getContents();
                    tvStatus.setText(getString(R.string.status_connecting_to, ip));
                    
                    SocketClient.connect(ip, PORT, new SocketClient.ConnectionListener() {
                        @Override
                        public void onConnected() {
                            runOnUiThread(() -> {
                                // Send user info to server
                                sendUserInfo();
                                
                                // Save connected user to database
                                saveConnectedUser(ip);
                                
                                // Navigate to ChatList
                                Intent intent = new Intent(QrConnectActivity.this, ChatListActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            runOnUiThread(() -> {
                                tvStatus.setText(R.string.status_connection_failed);
                                Toast.makeText(QrConnectActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                            });
                            Log.e(TAG, "Error connecting to socket client", e);
                        }
                    });
                }
            });

    private String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr != null && sAddr.indexOf(':') < 0) { // Check for null sAddr before indexOf
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error getting IP address", ex);
        }
        return null;
    }

    private void sendUserInfo() {
        String userId = userPreferences.getUserId();
        String name = userPreferences.getUserName();
        String mobileNumber = userPreferences.getMobileNumber();
        String deviceId = userPreferences.getDeviceId();
        String ipAddress = getIPAddress();

        if (userId != null && name != null) {
            String userInfoJson = MessageHandler.createUserInfoJson(
                userId, name, mobileNumber, deviceId, ipAddress
            );
            
            if (SocketServer.isConnected()) {
                SocketServer.sendMessage(userInfoJson);
            } else if (SocketClient.isConnected()) {
                SocketClient.sendMessage(userInfoJson);
            }
        }
    }

    private void saveConnectedUser(String ipAddress) {
        // This will be updated when we receive user info from the other device
        // For now, we'll create a temporary user entry
        // Proper user info will be saved when USER_INFO message is received
        Log.d(TAG, "Connected to: " + ipAddress);
    }
}
