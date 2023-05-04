package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    public static final String TAG = "MyApplication";
    Camera camera = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void CapturePhoto() {
        Log.d(TAG,"Capturing Photo");
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int backCamera=0;
        Camera.getCameraInfo(backCamera, cameraInfo);
        try {
            camera = Camera.open(backCamera);
            Camera.Parameters camParams = camera.getParameters();
            String supportedIsoValues = camParams.get("iso-values");
            camParams.set("iso", "100"); //Sets to nearest 100
            camera.setParameters(camParams);
        } catch (RuntimeException e) {
            Log.d(TAG,"Camera not available: " + 0);
            camera = null;
        }
        try {
            if (camera == null) {
                Log.d(TAG,"No Camera");
            } else {
                Log.d(TAG,"Got the camera");
                camera.setPreviewTexture(new SurfaceTexture(0));
                camera.startPreview();
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File directory= new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), TAG);
                        if (!directory.exists() && !directory.mkdirs()) {
                            directory.mkdirs();
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String date = dateFormat.format(new Date());
                        String photoFile = "FirstCapture" + "_" + date + ".jpg";
                        String file = directory.getPath() + File.separator + photoFile;
                        File mainPicture = new File(file);
                        try {
                            FileOutputStream fos = new FileOutputStream(mainPicture);
                            fos.write(data);
                            fos.close();
                            Log.d(TAG,"image saved at "+file);
                        } catch (Exception error) {
                            Log.d(TAG,"Image could not be saved");
                        }
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                            }
                        }, 1000);
                        Log.d(TAG,"Broadcasting to activity started");
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("positive");
                        broadcastIntent.putExtra("Data", "Broadcast Data");
                        sendBroadcast(broadcastIntent);
                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        String type = bundle.getString("TYPE");
        Log.d(TAG,"Type "+type);
        if(type.equals("camera"))
            CapturePhoto();
        else
            captureDifferentEv();
        return START_STICKY;
    }

    public void captureDifferentEv() {
        Log.d(TAG,"Camera reopened");
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int backCamera=0;
        Camera.getCameraInfo(backCamera, cameraInfo);
        try {
            Camera.Parameters camParams = camera.getParameters();
            String supportedIsoValues = camParams.get("iso-values");
            camParams.set("iso", "100");
        } catch (RuntimeException e) {
            Log.d(TAG,"Camera not available: " + 0);
            camera = null;
        }
        try {
            if (null == camera) {
                Log.d(TAG,"Could not get camera");
            } else {
                Log.d(TAG,"Got the camera");
                new CountDownTimer(11000,1000){
                    int step=-5;
                    @Override
                    public void onTick(long millisUntilFinished) {
                        try {
                            camera.setPreviewTexture(new SurfaceTexture(0));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        camera.startPreview();
                        Camera.Parameters camParams = camera.getParameters();
                        Log.d(TAG, String.valueOf(camParams.getMinExposureCompensation()));
                        camParams.setExposureCompensation(step++);
                        camera.setParameters(camParams);
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                File directory= new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES), "MyApplication");
                                if (!directory.exists() && !directory.mkdirs()) {
                                    directory.mkdirs();
                                }
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String date = dateFormat.format(new Date());
                                String photoFile;
                                photoFile = "EvPicture" + camParams.getExposureCompensation() + ".jpg";
                                String filename = directory.getPath() + File.separator + photoFile;
                                File mainPicture = new File(filename);
                                try {
                                    FileOutputStream fos = new FileOutputStream(mainPicture);
                                    fos.write(data);
                                    fos.close();
                                } catch (Exception error) {
                                    Log.d(TAG,"Image could not be saved");
                                }
                                Log.d(TAG,"Broadcasting to activity started");
                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction("evs");
                                broadcastIntent.putExtra("Data", "Capturing image for exposure "+camParams.getExposureCompensation());
                                sendBroadcast(broadcastIntent);
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("evs");
                        broadcastIntent.putExtra("Finish", "Sending Image");
                        sendBroadcast(broadcastIntent);
                        stopSelf();
                    }
                }.start();
            }
        } catch (Exception e) {
            camera.release();
        }
    }

    @Override
    public void onDestroy() {
        camera.release();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}