package com.maris_skrivelis.gada_projekts;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class NoInternetConnection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_connection);
        getSupportActionBar().hide();
        ImageView noInternetImage = findViewById(R.id.noInternetImage);

        //listens for click on image with "try again"
        noInternetImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //if network is back, close warning
                if(isNetworkAvailable()) {

                    //close warning
                    finish();

                    //restars app
                    startActivity(new Intent(NoInternetConnection.this , MainActivity.class));
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        //checks internet connection
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}