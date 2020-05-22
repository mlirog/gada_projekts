package com.maris_skrivelis.gada_projekts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        Handler handler = new Handler();

        //closes splashscreen after given time
        handler.postDelayed(new Runnable() {
            public void run() {
                //close welcome screen after given time
                finish();
                //starts app main activity
                startActivity(new Intent(SplashScreen.this , MainActivity.class));
            }
            //splashscreen time is stored in dimens file
        }, getResources().getInteger(R.integer.splashcreen_time));
    }
}
