package com.example.twilightlemon.lemonapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.sax.StartElementListener;
import android.view.WindowManager;

import com.example.twilightlemon.lemonapp.LemonApp;
import com.example.twilightlemon.lemonapp.R;

public class StartPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Integer time = 1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartPage.this, LemonApp.class));
                StartPage.this.finish();
            }
        }, time);
    }
}