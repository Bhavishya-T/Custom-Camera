package com.example.myapplication;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity {

    IntentFilter broadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkCameraPermissions(CameraActivity.this);
        setContentView(R.layout.acitivity_camera);
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcast = new IntentFilter();
        broadcast.addAction("positive");
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("TYPE","camera");
        Log.d("kkkk","service started");
        startService(serviceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("kkkk","activity resumed and got broadcast response");
                TextView view = findViewById(R.id.cameraText);
                view.setText("Got data");
//                Intent stopIntent = new Intent(CameraActivity.this,
//                        MyService.class);
//                stopService(stopIntent);
//                Log.d("kkkk", "service stopped and broadcast end");
                CircularProgressIndicator progressIndicator = (CircularProgressIndicator) findViewById(R.id.progress);
                progressIndicator.setProgress(100);
                new CountDownTimer(10000, 2000) {
                    public void onTick(long millisUntilFinished) {
                        //forward progress
                        long finishedSeconds = 10000 - millisUntilFinished;
                        int total = (int) (((float)finishedSeconds / (float)10000) * 100.0);
                        progressIndicator.setProgressCompat(total,true);

                    }
                    public void onFinish() {
                        // DO something when 1 minute is up
                        Log.d("kkkk", "next screen now");
                        progressIndicator.setProgressCompat(100,true);
                        Intent intent1 = new Intent(CameraActivity.this,CaptureActivity.class);
                        CameraActivity.this.startActivity(intent1);
                    }
                }.start();
            }
        }, broadcast);
    }

    public void checkCameraPermissions(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            Log.d("checkCameraPermissions", "No Camera Permissions");
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] { Manifest.permission.CAMERA },
                    100);
        }
    }
}
