package com.example.p.audio;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Handles audio recording using MediaRecorder
 */
public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    private MediaRecorder mediaRecorder;
    private String outputPath;
    private boolean isRecording = false;

    /**
     * Start recording audio
     */
    public String startRecording(File outputFile) {
        if (isRecording) {
            Log.w(TAG, "Already recording");
            return null;
        }

        try {
            outputPath = outputFile.getAbsolutePath();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputPath);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            Log.d(TAG, "Recording started: " + outputPath);
            return outputPath;

        } catch (IOException e) {
            Log.e(TAG, "Error starting recording", e);
            releaseRecorder();
            return null;
        }
    }

    /**
     * Stop recording
     */
    public String stopRecording() {
        if (!isRecording || mediaRecorder == null) {
            return null;
        }

        try {
            mediaRecorder.stop();
            isRecording = false;
            String path = outputPath;
            releaseRecorder();
            Log.d(TAG, "Recording stopped: " + path);
            return path;

        } catch (RuntimeException e) {
            Log.e(TAG, "Error stopping recording", e);
            releaseRecorder();
            return null;
        }
    }

    /**
     * Release MediaRecorder resources
     */
    private void releaseRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing recorder", e);
            }
            mediaRecorder = null;
        }
        isRecording = false;
    }

    /**
     * Check if currently recording
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Get current recording duration (if supported)
     */
    public long getDuration() {
        // MediaRecorder doesn't directly provide duration
        // Would need to track start time
        return 0;
    }

    /**
     * Cancel recording and delete file
     */
    public void cancelRecording() {
        if (isRecording) {
            stopRecording();
        }
        if (outputPath != null) {
            File file = new File(outputPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
