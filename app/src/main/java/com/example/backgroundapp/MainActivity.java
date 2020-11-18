package com.example.backgroundapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import static android.net.Uri.parse;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private BackgroundUpdatesService backgroundUpdatesService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service != null) {
                BackgroundUpdatesService.LocalBinder binder = (BackgroundUpdatesService.LocalBinder) service;
                backgroundUpdatesService = binder.getService();
            }

            startService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            backgroundUpdatesService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            }, 200);
            Intent ignoreIntent = new Intent();
            ignoreIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            ignoreIntent.setData(parse("package:" + getPackageName()));
            startActivity(ignoreIntent);
        }
    }

    @Override
    public void onStart() {
        bindService(new Intent(this, BackgroundUpdatesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    private void startService() {
        backgroundUpdatesService.create();
        backgroundUpdatesService.refreshNotification();
    }

}