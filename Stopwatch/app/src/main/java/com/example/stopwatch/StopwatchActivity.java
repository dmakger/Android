package com.example.stopwatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class StopwatchActivity extends Activity {

    private int milliseconds = 0;
    private boolean running = false;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Button modeButton = (Button)findViewById(R.id.mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        runTimer();
    }

    // Reset the stopwatch by clicking on the Reset button
    public void onClickReset(View view) {
        Button modeButton = (Button)findViewById(R.id.mode);

        running = false;
        milliseconds = 0;
        modeButton.setText("Start");
    }

    public void onClickMode(View view) {
        Button modeButton = (Button)findViewById(R.id.mode);

        // Stopwatch works
        if (running) {
            running = false;
            modeButton.setText("Start");
        } else {
            running = true;
            modeButton.setText("Stop");
        }
    }

    private void runTimer() {
        final TextView textView = (TextView) findViewById(R.id.time_view);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (running) {
                    milliseconds++;
                }

                int hours = milliseconds/3600/60;
                int minute = milliseconds/3600%60;
                int seconds = (milliseconds%3600)/60;
                int mills = milliseconds%60;

                String time;
                if (hours == 0) {
                    time = String.format("%02d:%02d,%02d",
                            minute, seconds, mills);
                } else {
                    time = String.format("%02d:%02d:%02d,%02d",
                            hours, minute, seconds, mills);
                }
                textView.setText(time);

                handler.postDelayed(this, 1);
            }
        });
    }
}