package com.spamblocker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private Switch enableSwitch;
    private TextView statusText;
    private Button permissionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        enableSwitch = findViewById(R.id.enableSwitch);
        statusText = findViewById(R.id.statusText);
        permissionsButton = findViewById(R.id.permissionsButton);
    }

    private void setupClickListeners() {
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !hasAllPermissions()) {
                enableSwitch.setChecked(false);
                requestPermissions();
                return;
            }

            if (isChecked) {
                startCallDetectionService();
            } else {
                stopCallDetectionService();
            }
            updateUI();
        });

        permissionsButton.setOnClickListener(v -> requestPermissions());
    }

    private void startCallDetectionService() {
        Intent serviceIntent = new Intent(this, CallDetectionService.class);
        startForegroundService(serviceIntent);
        Toast.makeText(this, "Spam call blocker activated", Toast.LENGTH_SHORT).show();
    }

    private void stopCallDetectionService() {
        Intent serviceIntent = new Intent(this, CallDetectionService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Spam call blocker deactivated", Toast.LENGTH_SHORT).show();
    }

    private boolean hasAllPermissions() {
        String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return Settings.canDrawOverlays(this);
    }

    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);

        // Request overlay permission
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            updateUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        boolean hasPermissions = hasAllPermissions();
        boolean serviceRunning = CallDetectionService.isRunning();

        enableSwitch.setEnabled(hasPermissions);
        enableSwitch.setChecked(serviceRunning);

        if (!hasPermissions) {
            statusText.setText("Permissions required");
            statusText.setTextColor(getColor(android.R.color.holo_red_dark));
            permissionsButton.setEnabled(true);
        } else if (serviceRunning) {
            statusText.setText("Active - Blocking spam calls");
            statusText.setTextColor(getColor(android.R.color.holo_green_dark));
            permissionsButton.setEnabled(false);
        } else {
            statusText.setText("Ready - Toggle to activate");
            statusText.setTextColor(getColor(android.R.color.holo_orange_dark));
            permissionsButton.setEnabled(false);
        }
    }
}