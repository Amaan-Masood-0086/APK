package com.example.p.audio;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Handles audio playback using MediaPlayer
 */
public class AudioPlayer {
    private static final String TAG = "AudioPlayer";
    private MediaPlayer mediaPlayer;
    private String currentFilePath;
    private PlaybackListener listener;

    public interface PlaybackListener {
        void onPlaybackComplete();
        void onPlaybackError(Exception e);
    }

    public void setPlaybackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * Play audio file
     */
    public boolean play(String filePath) {
        if (isPlaying()) {
            stop();
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentFilePath = filePath;

            mediaPlayer.setOnCompletionListener(mp -> {
                if (listener != null) {
                    listener.onPlaybackComplete();
                }
                releasePlayer();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Playback error: " + what + ", " + extra);
                if (listener != null) {
                    listener.onPlaybackError(new Exception("Playback error: " + what));
                }
                releasePlayer();
                return true;
            });

            Log.d(TAG, "Playing: " + filePath);
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error playing audio", e);
            if (listener != null) {
                listener.onPlaybackError(e);
            }
            releasePlayer();
            return false;
        }
    }

    /**
     * Pause playback
     */
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.d(TAG, "Playback paused");
        }
    }

    /**
     * Resume playback
     */
    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d(TAG, "Playback resumed");
        }
    }

    /**
     * Stop playback
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            releasePlayer();
            Log.d(TAG, "Playback stopped");
        }
    }

    /**
     * Check if playing
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * Get current position (milliseconds)
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Get duration (milliseconds)
     */
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * Seek to position
     */
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * Release MediaPlayer resources
     */
    private void releasePlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing player", e);
            }
            mediaPlayer = null;
        }
        currentFilePath = null;
    }

    /**
     * Get current file path
     */
    public String getCurrentFilePath() {
        return currentFilePath;
    }
}
