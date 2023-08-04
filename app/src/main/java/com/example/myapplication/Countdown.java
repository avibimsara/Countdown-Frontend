package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
//import android.telecom.Call;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Countdown extends AppCompatActivity {

    private TextView countdownTextView;
    private long countdownTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        countdownTextView = findViewById(R.id.countdownTextView);

        // Fetch countdown parameter from the server
        fetchCountdownParameterFromServer();
    }

    private void fetchCountdownParameterFromServer() {
        // Make an API call to your server to fetch the countdown parameter
        // Example using Retrofit:
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<CountdownResponse> call = apiService.getCountdownParameter();
        call.enqueue(new Callback<CountdownResponse>() {
            @Override
            public void onResponse(Call<CountdownResponse> call, Response<CountdownResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Retrieve the countdown parameter from the server response
                    long countdownInSeconds = response.body().getCountdownInSeconds();
                    countdownTimeInMillis = countdownInSeconds * 1000;

                    // Start the countdown timer
                    startCountdownTimer();
                } else {
                    // Handle the error case if the API call returns an unsuccessful response
                    Toast.makeText(Countdown.this, "Failed to fetch countdown parameter", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountdownResponse> call, Throwable t) {
                // Handle the error case if the API call fails
                Toast.makeText(Countdown.this, "Failed to fetch countdown parameter", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startCountdownTimer() {
        new CountDownTimer(countdownTimeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                String countdownText = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        secondsRemaining / 3600, (secondsRemaining % 3600) / 60, secondsRemaining % 60);
                countdownTextView.setText(countdownText);
            }

            public void onFinish() {
                countdownTextView.setText("00:00:00");
            }
        }.start();
    }
}