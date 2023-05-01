package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class CaptureActivity extends AppCompatActivity {

    IntentFilter broadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcast = new IntentFilter();
        broadcast.addAction("evs");
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("TYPE","capture");
        Log.d("kkkk","service started for captures");
        startService(serviceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("kkkk","activity resumed and got broadcast response for captures");
                TextView view = findViewById(R.id.captureText);
                view.setText("Got capture");
                Intent stopIntent = new Intent(CaptureActivity.this,
                        MyService.class);
                stopService(stopIntent);
                Log.d("kkkk", "service stopped and broadcast end");
//                CircularProgressIndicator progressIndicator = (CircularProgressIndicator) findViewById(R.id.progress);
//                progressIndicator.setProgress(100);
//                new CountDownTimer(10000, 2000) {
//                    public void onTick(long millisUntilFinished) {
//                        //forward progress
//                        long finishedSeconds = 10000 - millisUntilFinished;
//                        int total = (int) (((float)finishedSeconds / (float)10000) * 100.0);
//                        progressIndicator.setProgressCompat(total,true);
//
//                    }
//                    public void onFinish() {
//                        // DO something when 1 minute is up
//                        Log.d("kkkk", "next screen now");
//                        progressIndicator.setProgressCompat(100,true);
//                        Intent intent1 = new Intent(CameraActivity.this,CaptureActivity.class);
//                        CameraActivity.this.startActivity(intent1);
//                    }
//                }.start();
            }
        }, broadcast);
    }
}
