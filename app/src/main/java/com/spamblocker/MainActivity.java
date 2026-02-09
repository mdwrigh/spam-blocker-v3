package com.spamblocker;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "SpamBlocker";
    private MediaPlayer mediaPlayer;
    private Button playFaxToneButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playFaxToneButton = findViewById(R.id.playFaxToneButton);
        statusText = findViewById(R.id.statusText);

        playFaxToneButton.setOnClickListener(v -> playFaxTone());

        statusText.setText("Spam Call Blocker Ready");
    }

    private void playFaxTone() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            
            // For now, just show a message since we don't have the audio file
            statusText.setText("Playing fax tone...");
            playFaxToneButton.setText("Stop Fax Tone");
            playFaxToneButton.setOnClickListener(v -> stopFaxTone());
            Toast.makeText(this, "Fax tone playing", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error playing fax tone", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopFaxTone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        statusText.setText("Spam Call Blocker Ready");
        playFaxToneButton.setText("Play Fax Tone");
        playFaxToneButton.setOnClickListener(v -> playFaxTone());
        Toast.makeText(this, "Fax tone stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
