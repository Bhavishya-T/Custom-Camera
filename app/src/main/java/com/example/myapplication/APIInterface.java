package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers("Content-Type:application/json")
    @POST("/api/v1/auth/sign_in")
    Call<SignInResponse> signIn(@Body SignInRequest request);

    @Multipart
    @Headers("Content-Type:application/json")
    @POST("/api/v1/tests")
    Call<TestResponse> sendImage(@Header("access-token") String access_token, @Header("uid") String uid,
                                 @Header("client") String client, @Body TestRequest request);
}
