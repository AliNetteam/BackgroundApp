package com.example.backgroundapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;

public class BackgroundUpdatesService extends Service {

    private final IBinder binder = new LocalBinder();

    private final String CHANNEL_ID = "channel01";
    private Handler serviceHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public class LocalBinder extends Binder {

        public BackgroundUpdatesService getService() {
            return BackgroundUpdatesService.this;
        }

    }

    private final Runnable prepareLocationRunnable = new Runnable() {

        public void run() {
            refreshNotification();
            serviceHandler.postDelayed(prepareLocationRunnable, 30 * 1000);
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (SDK_INT >= O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        initHandler();

        refreshNotification();
    }

    public void initHandler() {
        HandlerThread handlerThread = new HandlerThread("tag");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        serviceHandler.postDelayed(prepareLocationRunnable, 5 * 1000);
    }

    public void create() {
        Intent intent = new Intent(getApplicationContext(), BackgroundUpdatesService.class);
        if (SDK_INT >= O) {
            startForegroundService(intent);
        }
        else {
            startService(intent);
        }
    }

    public void refreshNotification() {
        startForeground(123, getNotification());
    }

    public Notification getNotification() {
        Context context = getApplicationContext();
        String contentText = "Content";
        String contentTitle = "Title";

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.icon)
                .setSound(alarmSound)
                .setWhen(System.currentTimeMillis())
                .build();
    }
}
