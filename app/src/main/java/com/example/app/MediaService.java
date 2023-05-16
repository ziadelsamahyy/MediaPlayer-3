package com.example.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MediaService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String CHANNEL_ID = "MediaServiceChannel";
    private MediaPlayer mediaPlayer;

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startForeground(1, createNotification("Paused"));
        }
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startForeground(1, createNotification("Playing"));
        }
    }

    public class LocalBinder extends Binder {
        MediaService getService() {
            return MediaService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY":
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(this, R.raw.a);
                        mediaPlayer.setOnCompletionListener(this);
                    }
                    mediaPlayer.start();
                    startForeground(1, createNotification("Playing"));
                    break;
                case "PAUSE":
                    mediaPlayer.pause();
                    startForeground(1, createNotification("Paused"));
                    break;
                case "RESUME":
                    mediaPlayer.start();
                    startForeground(1, createNotification("Playing"));
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }

    private Notification createNotification(String status) {
        Intent playIntent = new Intent(this, MediaService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent,  PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MediaService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent,  PendingIntent.FLAG_IMMUTABLE);

        Intent resumeIntent = new Intent(this, MediaService.class);
        resumeIntent.setAction("RESUME");
        PendingIntent resumePendingIntent = PendingIntent.getService(this, 0, resumeIntent,  PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Media Player")
                .setContentText(status)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(R.drawable.play, "Play", playPendingIntent)
                .addAction(R.drawable.pause, "Pause", pausePendingIntent)
                .addAction(R.drawable.resume, "Resume", resumePendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Media Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}