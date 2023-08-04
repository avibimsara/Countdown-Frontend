package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
public class CountdownResponse {
    @SerializedName("countdownInSeconds")
    private long countdownInSeconds;

    public long getCountdownInSeconds() {
        return countdownInSeconds;
    }
}
