package com.example.p.network;

import android.content.Context;
import android.util.Log;

import com.example.p.database.Message;
import com.example.p.repository.MessageRepository;
import com.example.p.utils.FileStorageManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Handles file transfer over TCP sockets
 */
public class FileTransferManager {
    private static final String TAG = "FileTransferManager";
    private static final int CHUNK_SIZE = 64 * 1024; // 64KB chunks

    private FileStorageManager fileStorageManager;
    private MessageRepository messageRepository;
    private Context context;

    public FileTransferManager(Context context) {
        this.context = context.getApplicationContext();
        this.fileStorageManager = new FileStorageManager(context);
        this.messageRepository = new MessageRepository((android.app.Application) context);
    }

    /**
     * Send file in chunks
     */
    public void sendFile(String filePath, String chatId, String senderId, 
                        String senderName, String messageId, boolean isGroupMessage) {
        new Thread(() -> {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    Log.e(TAG, "File not found: " + filePath);
                    return;
                }

                String fileName = file.getName();
                String fileType = getFileType(fileName);
                long fileSize = file.length();
                int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);

                Log.d(TAG, "Sending file: " + fileName + " (" + totalChunks + " chunks)");

                byte[] fileData = fileStorageManager.readFile(filePath);

                for (int i = 0; i < totalChunks; i++) {
                    int start = i * CHUNK_SIZE;
                    int end = Math.min(start + CHUNK_SIZE, (int) fileSize);
                    byte[] chunk = new byte[end - start];
                    System.arraycopy(fileData, start, chunk, 0, end - start);

                    // Encode chunk to base64
                    String chunkData = Base64.getEncoder().encodeToString(chunk);

                    // Create file message
                    String fileMessage = MessageProtocol.createFileMessage(
                        messageId, chatId, senderId, senderName, fileName, fileType,
                        fileSize, i, totalChunks, chunkData, isGroupMessage
                    );

                    // Send via socket
                    if (EnhancedSocketServer.isRunning()) {
                        EnhancedSocketServer.broadcastMessage(fileMessage);
                    }

                    // Small delay between chunks
                    Thread.sleep(10);
                }

                Log.d(TAG, "File sent successfully: " + fileName);

            } catch (Exception e) {
                Log.e(TAG, "Error sending file", e);
            }
        }).start();
    }

    /**
     * Receive and assemble file chunks
     */
    public void receiveFileChunk(String messageJson) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(messageJson);
            String messageId = json.optString("messageId");
            String fileName = json.optString("fileName");
            int chunkIndex = json.optInt("chunkIndex");
            int totalChunks = json.optInt("totalChunks");
            String chunkData = json.optString("chunkData");
            String fileType = json.optString("fileType");

            // Decode chunk
            byte[] chunk = Base64.getDecoder().decode(chunkData);

            // Save chunk (in real implementation, use a chunk manager)
            // For now, we'll save complete file when last chunk received
            if (chunkIndex == totalChunks - 1) {
                // Last chunk - assemble file
                // Note: This is simplified - should use proper chunk management
                String filePath = fileStorageManager.saveFile(chunk, fileName, fileType);

                // Update message with file path
                // In real implementation, track chunks and assemble properly
                Log.d(TAG, "File received: " + fileName);

                // Send acknowledgment
                String ack = MessageProtocol.createAck(messageId, "DELIVERED");
                if (EnhancedSocketServer.isRunning()) {
                    EnhancedSocketServer.broadcastMessage(ack);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error receiving file chunk", e);
        }
    }

    /**
     * Get file type from extension
     */
    private String getFileType(String fileName) {
        String extension = "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            extension = fileName.substring(lastDot + 1).toUpperCase();
        }

        switch (extension) {
            case "PDF":
                return "PDF";
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                return "IMAGE";
            case "MP3":
            case "WAV":
            case "3GP":
            case "MP4":
                return "AUDIO";
            default:
                return "FILE";
        }
    }
}
