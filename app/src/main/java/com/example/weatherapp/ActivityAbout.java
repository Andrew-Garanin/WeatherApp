package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView t2 =  findViewById(R.id.weather_channel);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton buttonGoMain;
        buttonGoMain = findViewById(R.id.button_gomain);
        buttonGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityAbout.super.finish();
            }
        });
    }
}