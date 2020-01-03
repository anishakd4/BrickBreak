package com.developer.anishakd4.breakout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;

public class MainActivity extends AppCompatActivity {

    int height, width;
    BreakoutEngine breakoutEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenDimension();
        breakoutEngine = new BreakoutEngine(this, height, width);
        setContentView(breakoutEngine);
    }

    private void getScreenDimension(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    @Override
    protected void onPause() {
        super.onPause();
        breakoutEngine.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        breakoutEngine.onResume();
    }
}
