package com.example.myapplication;

import com.example.myapplication.SignInRequest;
import com.example.myapplication.SignInResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface APIInterface {
    @Headers("Content-Type:application/json")
    @POST("/api/v1/auth/sign_in")
    Call<SignInResponse> signIn(@Body SignInRequest request);

    @Multipart
    @Headers("Content-Type:application/json")
    @POST("/api/v1/tests")
    Call<Object> sendImage(@Header("access-token") String access_token, @Header("uid") String uid,
                           @Header("client") String client, @PartMap Map<String, RequestBody> params);
}
