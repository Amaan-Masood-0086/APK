package com.example.p.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Manages file storage in app directory
 */
public class FileStorageManager {
    private static final String TAG = "FileStorageManager";
    private static final String FILES_DIR = "files";
    private static final String AUDIO_DIR = "audio";
    private static final String IMAGES_DIR = "images";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    private Context context;
    private File filesDir;
    private File audioDir;
    private File imagesDir;

    public FileStorageManager(Context context) {
        this.context = context.getApplicationContext();
        this.filesDir = new File(context.getFilesDir(), FILES_DIR);
        this.audioDir = new File(context.getFilesDir(), AUDIO_DIR);
        this.imagesDir = new File(context.getFilesDir(), IMAGES_DIR);

        // Create directories if they don't exist
        createDirectories();
    }

    /**
     * Create necessary directories
     */
    private void createDirectories() {
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
    }

    /**
     * Save file to app storage
     */
    public String saveFile(InputStream inputStream, String fileName, String fileType) throws IOException {
        File targetDir = getDirectoryForType(fileType);
        File file = new File(targetDir, fileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalBytes = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            totalBytes += bytesRead;
            if (totalBytes > MAX_FILE_SIZE) {
                outputStream.close();
                file.delete();
                throw new IOException("File too large");
            }
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        Log.d(TAG, "File saved: " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * Save file from byte array
     */
    public String saveFile(byte[] data, String fileName, String fileType) throws IOException {
        File targetDir = getDirectoryForType(fileType);
        File file = new File(targetDir, fileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);
        outputStream.close();

        Log.d(TAG, "File saved: " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * Read file as byte array
     */
    public byte[] readFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        FileInputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        inputStream.close();

        return data;
    }

    /**
     * Get file input stream
     */
    public InputStream getFileInputStream(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return new FileInputStream(file);
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Delete file
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Get file size
     */
    public long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * Get directory for file type
     */
    private File getDirectoryForType(String fileType) {
        if (fileType == null) {
            return filesDir;
        }

        switch (fileType.toUpperCase()) {
            case "VOICE":
            case "AUDIO":
                return audioDir;
            case "IMAGE":
            case "JPG":
            case "JPEG":
            case "PNG":
                return imagesDir;
            default:
                return filesDir;
        }
    }

    /**
     * Clean up old files (older than specified days)
     */
    public void cleanupOldFiles(int daysOld) {
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L);
        cleanupDirectory(filesDir, cutoffTime);
        cleanupDirectory(audioDir, cutoffTime);
        cleanupDirectory(imagesDir, cutoffTime);
    }

    private void cleanupDirectory(File dir, long cutoffTime) {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.lastModified() < cutoffTime) {
                file.delete();
                Log.d(TAG, "Deleted old file: " + file.getName());
            }
        }
    }
}
