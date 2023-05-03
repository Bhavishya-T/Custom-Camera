package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class SignInResponse {
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String onboarded;
    @SerializedName("id")
    int id;

    public SignInResponse(){

    }

    public SignInResponse(String email,String password,int id){
        this.email = email;
        this.onboarded = password;
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getOnboarded() {
        return onboarded;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOnboarded(String onboarded) {
        this.onboarded = onboarded;
    }
}
