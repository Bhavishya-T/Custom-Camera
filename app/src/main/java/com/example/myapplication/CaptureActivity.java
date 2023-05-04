package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptureActivity extends AppCompatActivity {

    public static final String TAG = "MyApplication";

    IntentFilter broadcast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
    }

    @Override
    public void onStart() {
        super.onStart();
        createBroadcast();
        createService();
    }

    public void createService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("TYPE","capture");
        Log.d(TAG,"service started for captures");
        startService(serviceIntent);
    }

    public void createBroadcast() {
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
                TextInputEditText view = findViewById(R.id.testStatus);
                String doneMessage = intent.getStringExtra("Finish");
                if(doneMessage!=null){
                    message=doneMessage;
                }
                view.setText(message);
                if(doneMessage!=null){
                    doSignIn();
                }
            }
        }, broadcast);
    }

    public void doSignIn() {
        Log.d(TAG,"Inside doSignIn");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<SignInResponse> call = apiInterface.signIn(new SignInRequest("amit_4@test.com","12345678"));
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                Headers headers = response.headers();
                Log.d(TAG,headers.get("access-token")+" "+headers.get("uid")+" "
                +headers.get("client"));
                sendImage(headers);
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d(TAG,"Error "+t.getMessage());
            }
        });
    }

    public void sendImage(Headers headers) {
        File pictureFileDir= new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApplication");
        String filename = pictureFileDir.getPath() + File.separator + "EvPicture0.jpg";
        File file = new File(filename);
        Log.d(TAG,"Image being sent is "+filename);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String date = new Date().toString();
        RequestBody done_date =
                RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody reason =
                RequestBody.create(MediaType.parse("text/plain"), "NA");
        RequestBody failure =
                RequestBody.create(MediaType.parse("application/json"), String.valueOf(false));
        Map<String, RequestBody> map = new HashMap<>();
        map.put("done_date",done_date);
        map.put("images_attributes", requestFile);
//        map.put("batch_qr_code","AAO");
        map.put("reason",reason);
        map.put("failure",failure);
        Log.d(TAG,"Inside sendImage");
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Object> call = apiInterface.sendImage(headers.get("access-token"),headers.get("uid"),
                headers.get("client"), map);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                TextInputEditText view = findViewById(R.id.testStatus);
                view.setText("Test Done");
                Log.d(TAG,"Response "+response.body());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                TextInputEditText view = findViewById(R.id.testStatus);
                view.setText("Test Failed");
                Log.d(TAG,"Error "+t.getMessage());
            }
        });
    }
}
