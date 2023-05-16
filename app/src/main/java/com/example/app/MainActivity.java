package com.example.app;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button playButton, pauseButton, resumeButton;
    private MediaService mediaService;
    private boolean isBound;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder instanceof MediaService.LocalBinder) {
                MediaService.LocalBinder binder = (MediaService.LocalBinder) iBinder;
                mediaService = binder.getService();
                isBound = true;
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        resumeButton = findViewById(R.id.resume_button);

        playButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MediaService.class);
            intent.setAction("PLAY");
            startService(intent);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        });

        pauseButton.setOnClickListener(view -> {
            if (isBound && mediaService != null) {
                mediaService.pause();
            }
        });

        resumeButton.setOnClickListener(view -> {
            if (isBound && mediaService != null) {
                mediaService.resume();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}