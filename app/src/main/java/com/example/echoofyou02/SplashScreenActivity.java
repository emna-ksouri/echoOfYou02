package com.example.echoofyou02;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Duration for the splash screen
        int SPLASH_SCREEN_DURATION = 3000;

        // Handler to delay the transition to the main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Navigate to the next activity
            Intent intent = new Intent(SplashScreenActivity.this, SignupActivity.class);
            startActivity(intent);


            finish();
        }, SPLASH_SCREEN_DURATION);
    }
}
