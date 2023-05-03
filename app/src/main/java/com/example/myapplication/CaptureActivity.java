package com.example.myapplication;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                String message = intent.getStringExtra("Data");
                TextView view = findViewById(R.id.captureText);
                String doneMessage = intent.getStringExtra("Finish");
                if(doneMessage!=null){
                    message=doneMessage;
                }
                view.setText(message);
                if(doneMessage!=null){
                    doCall();
                }
//                Intent stopIntent = new Intent(CaptureActivity.this,
//                        MyService.class);
//                stopService(stopIntent);
//                Log.d("kkkk", "service stopped and broadcast end");
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

    private void doCall() {
        Log.d("kkkk","Inside doCall");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<SignInResponse> call = apiInterface.signIn(new SignInRequest("amit_4@test.com","12345678"));
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                Headers headers = response.headers();
                Log.d("kkkk",headers.get("access-token")+" "+headers.get("uid")+" "
                +headers.get("client"));
                sendImage(headers);
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d("kkkk","Error "+t.getMessage());
            }
        });
    }

    private void sendImage(Headers headers) {
        Log.d("kkkk","Inside sendImage");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<TestResponse> call = apiInterface.sendImage(headers.get("access-token"),headers.get("uid"),
                headers.get("client"), new TestRequest());
        call.enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
//                Log.d("kkkk",headers.get("access-token")+" "+headers.get("uid")+" "
//                        +headers.get("client"));
//                sendImage();
            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
//                Log.d("kkkk","Error "+t.getMessage());
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
