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

    public static final String TAG = "MyApplication";

    IntentFilter broadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createBroadcast();
        createService();
    }

    private void createService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("TYPE","capture");
        Log.d(TAG,"service started for captures");
        startService(serviceIntent);
    }

    private void createBroadcast() {
        broadcast = new IntentFilter();
        broadcast.addAction("evs");
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"activity resumed and got broadcast response for captures");
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
            }
        }, broadcast);
    }

    private void doCall() {
        Log.d(TAG,"Inside doCall");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<SignInResponse> call = apiInterface.signIn(new SignInRequest("amit_4@test.com","12345678"));
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                Headers headers = response.headers();
                TextView textView = findViewById(R.id.captureText);
                textView.setText("User is "+response.body().email);
                Log.d(TAG,headers.get("access-token")+" "+headers.get("uid")+" "
                +headers.get("client"));
//                sendImage(headers);
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d(TAG,"Error "+t.getMessage());
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

            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
//                Log.d("kkkk","Error "+t.getMessage());
            }
        });
    }
}
