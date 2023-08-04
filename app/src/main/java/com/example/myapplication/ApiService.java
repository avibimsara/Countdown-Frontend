package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface ApiService {

    // Endpoint to get the countdown parameter from the server
    @GET("countdown")
    Call<CountdownResponse> getCountdownParameter();

    // Endpoint to update the countdown parameter on the server
    @POST("countdown")
    Call<Void> updateCountdownParameter(@Body CountdownRequest countdownRequest);
}
