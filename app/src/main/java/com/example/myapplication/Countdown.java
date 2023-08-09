package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.telecom.Call;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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
        // Making an API call to server to fetch the countdown parameter
        // Using Retrofit:
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<CountdownResponse> call = apiService.getCountdownParameter();
        call.enqueue(new Callback<CountdownResponse>() {
            @Override
            public void onResponse(Call<CountdownResponse> call, Response<CountdownResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Retrieving the countdown parameter from the server response
                    long countdownInSeconds = response.body().getCountdownInSeconds();
                    countdownTimeInMillis = countdownInSeconds * 1000;

                    // Starting the countdown timer
                    startCountdownTimer();
                } else {
                    // Handling the error case if the API call returns an unsuccessful response
                    Toast.makeText(Countdown.this, "Failed to fetch countdown parameter", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountdownResponse> call, Throwable t) {
                // Handling the error case if the API call fails
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
                sendNotification("Time Over", "Countdown App");
            }
        }.start();
    }

    public void sendNotification (String message, String title ){

        Intent intent = new Intent(getApplicationContext(), Countdown.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_IMMUTABLE);


        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.stopwatch)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}