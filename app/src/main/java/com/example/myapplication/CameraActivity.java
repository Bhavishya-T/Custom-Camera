package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CameraActivity extends AppCompatActivity {
    public static final String TAG = "MyApplication";

    IntentFilter broadcast;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        setContentView(R.layout.acitivity_camera);
    }

    @Override
    public void onStart() {
        super.onStart();
        createBroadcast();
        createService();
    }

    public void createService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("TYPE","camera");
        Log.d(TAG,"service started");
        startService(serviceIntent);
    }

    public void createBroadcast() {
        broadcast = new IntentFilter();
        broadcast.addAction("positive");
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"activity resumed and got broadcast response");
                TextInputEditText view = findViewById(R.id.cameraStatus);
                view.setText("Capturing Single Image");
                CircularProgressIndicator progressIndicator = (CircularProgressIndicator) findViewById(R.id.progress);
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setProgress(100);
                new CountDownTimer(10000, 2000) {
                    public void onTick(long millisUntilFinished) {
                        long finishedSeconds = 10000 - millisUntilFinished;
                        int total = (int) (((float)finishedSeconds / (float)10000) * 100.0);
                        progressIndicator.setProgressCompat(total,true);

                    }
                    public void onFinish() {
                        Log.d(TAG, "next screen now");
                        progressIndicator.setProgressCompat(100,true);
                        Intent newIntent = new Intent(CameraActivity.this,CaptureActivity.class);
                        CameraActivity.this.startActivity(newIntent);
                    }
                }.start();
            }
        }, broadcast);
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
