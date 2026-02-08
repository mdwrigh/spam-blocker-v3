package com.spamblocker;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class FaxTonePlayer {
    private static final String TAG = "FaxTonePlayer";
    private static MediaPlayer mediaPlayer;
    private static AudioManager audioManager;

    public static void playFaxTone(Context context) {
        try {
            stopFaxTone(); // Stop any existing playback

            // Set audio to call mode
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(false);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            }

            // Create and configure MediaPlayer
            mediaPlayer = MediaPlayer.create(context, R.raw.fax_tone);
            if (mediaPlayer != null) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mediaPlayer.setLooping(true);
                
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.d(TAG, "Starting fax tone playback");
                    mp.start();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    return false;
                });

                mediaPlayer.prepareAsync();
            } else {
                Log.e(TAG, "Failed to create MediaPlayer");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error playing fax tone", e);
        }
    }

    public static void stopFaxTone() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "Fax tone stopped");
            }

            // Reset audio mode
            if (audioManager != null) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping fax tone", e);
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}