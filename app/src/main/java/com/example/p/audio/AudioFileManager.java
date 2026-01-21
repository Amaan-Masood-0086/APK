package com.example.p.audio;

import android.content.Context;
import android.util.Log;

import com.example.p.utils.FileStorageManager;

import java.io.File;

/**
 * Manages audio file storage and operations
 */
public class AudioFileManager {
    private static final String TAG = "AudioFileManager";
    private static final String AUDIO_PREFIX = "voice_";
    private static final String AUDIO_EXTENSION = ".3gp";
    private static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024; // 10MB

    private FileStorageManager fileStorageManager;
    private Context context;

    public AudioFileManager(Context context) {
        this.context = context.getApplicationContext();
        this.fileStorageManager = new FileStorageManager(context);
    }

    /**
     * Create new audio file for recording
     */
    public File createAudioFile() {
        String fileName = AUDIO_PREFIX + System.currentTimeMillis() + AUDIO_EXTENSION;
        File audioDir = new File(context.getFilesDir(), "audio");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        return new File(audioDir, fileName);
    }

    /**
     * Get audio file path
     */
    public String getAudioFilePath(String fileName) {
        File audioDir = new File(context.getFilesDir(), "audio");
        File file = new File(audioDir, fileName);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    /**
     * Check if audio file exists
     */
    public boolean audioFileExists(String fileName) {
        File audioDir = new File(context.getFilesDir(), "audio");
        File file = new File(audioDir, fileName);
        return file.exists();
    }

    /**
     * Delete audio file
     */
    public boolean deleteAudioFile(String fileName) {
        File audioDir = new File(context.getFilesDir(), "audio");
        File file = new File(audioDir, fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Get audio file size
     */
    public long getAudioFileSize(String fileName) {
        File audioDir = new File(context.getFilesDir(), "audio");
        File file = new File(audioDir, fileName);
        return file.exists() ? file.length() : 0;
    }

    /**
     * Validate audio file size
     */
    public boolean isValidAudioSize(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.length() <= MAX_AUDIO_SIZE;
    }

    /**
     * Clean up old audio files (older than specified days)
     */
    public void cleanupOldAudioFiles(int daysOld) {
        fileStorageManager.cleanupOldFiles(daysOld);
        Log.d(TAG, "Cleaned up audio files older than " + daysOld + " days");
    }
}
