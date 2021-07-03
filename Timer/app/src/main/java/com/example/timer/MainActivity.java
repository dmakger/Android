package com.example.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText hours;
    private EditText minutes;
    private EditText seconds;

    private TextView hoursText;
    private TextView minutesText;
    private TextView secondsText;

    private Button start;
    private Button cancel;
    private Button mode;

    private MediaPlayer sound;

    private boolean running = false;

    private TimeHelper timeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hours = findViewById(R.id.hours);
        minutes = findViewById(R.id.minutes);
        seconds = findViewById(R.id.seconds);

        hoursText = findViewById(R.id.hours_text);
        minutesText = findViewById(R.id.minutes_text);
        secondsText = findViewById(R.id.seconds_text);

        start = findViewById(R.id.start);
        cancel = findViewById(R.id.cancel);
        mode = findViewById(R.id.mode);

        sound = MediaPlayer.create(this, R.raw.never_gonna_give_you_up);

        timeHelper = new TimeHelper(hoursText, minutesText, secondsText);

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean("running");

            hours.setText(savedInstanceState.getString("hours"));
            minutes.setText(savedInstanceState.getString("minutes"));
            seconds.setText(savedInstanceState.getString("seconds"));

            hoursText.setText(savedInstanceState.getString("hoursText"));
            minutesText.setText(savedInstanceState.getString("minutesText"));
            secondsText.setText(savedInstanceState.getString("secondsText"));

            // Если был запущен код
            if (running || secondsText.getText().length() != 0) {
                start.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                mode.setVisibility(View.VISIBLE);
                mode.setText("Pause");

                // Убираем возможность изменять
                MainActivityHelper.TextViewVisible(hours, hoursText);
                MainActivityHelper.TextViewVisible(minutes, minutesText);
                MainActivityHelper.TextViewVisible(seconds, secondsText);
            }
        }

        runTimer();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("running", running);

        outState.putString("hours", hours.getText().toString());
        outState.putString("minutes", minutes.getText().toString());
        outState.putString("seconds", seconds.getText().toString());

        outState.putString("hoursText", hoursText.getText().toString());
        outState.putString("minutesText", minutesText.getText().toString());
        outState.putString("secondsText", secondsText.getText().toString());

    }

    private void runTimer() {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                System.out.println("Bruh");
                boolean isEnd;
                if (running) {
                    isEnd = timeHelper.minusSecond();
                    if (isEnd) {
                        stopTimer();
                        soundPlay();
                    }
                }
            }
        });
    }

    private void stopTimer() {
        running = false;
        cancel.setVisibility(View.GONE);
        mode.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);

        // Даем возможность редактирования пользователю
        MainActivityHelper.EditTextVisible(hours, hoursText);
        MainActivityHelper.EditTextVisible(minutes, minutesText);
        MainActivityHelper.EditTextVisible(seconds, secondsText);

        hoursText.setText("");
        minutesText.setText("");
        secondsText.setText("");
    }

    private void soundPlay() {
        sound.start();
    }

    public void onClickStart(View view) {
        boolean isHoursZero = MainActivityHelper.isZero(hours);
        boolean isMinutesZero = MainActivityHelper.isZero(minutes);
        boolean isSecondsZero = MainActivityHelper.isZero(seconds);
        System.out.println("[" + isHoursZero + " " + isMinutesZero + " " + isSecondsZero + "]");

        if (!isHoursZero || !isMinutesZero || !isSecondsZero) {
            running = true;
            start.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            mode.setVisibility(View.VISIBLE);
            mode.setText("Pause");

            // Убираем возможность изменять
            MainActivityHelper.TextViewVisible(hours, hoursText);
            MainActivityHelper.TextViewVisible(minutes, minutesText);
            MainActivityHelper.TextViewVisible(seconds, secondsText);

            // Получаем введенные данные
            int hoursValue = MainActivityHelper.getInt(hours);
            int minutesValue = MainActivityHelper.getInt(minutes);
            int secondsValue = MainActivityHelper.getInt(seconds);

            // Приводим в правильный вид
            if (secondsValue > 59) {
                minutesValue += secondsValue / 60;
                secondsValue %= 60;
            }
            if (minutesValue > 59) {
                hoursValue += minutesValue / 60;
                minutesValue %= 60;
            }

            // Устанавливаем TextView значения
            hoursText.setText(String.valueOf(hoursValue));
            minutesText.setText(String.valueOf(minutesValue));
            secondsText.setText(String.valueOf(secondsValue));
        }
    }

    public void onClickCancel(View view) {
        stopTimer();
    }

    public void onClickMode(View view) {
        if (!running) {
            running = true;
            mode.setText("Pause");
        } else {
            running = false;
            mode.setText("Resume");
        }
    }
}

class MainActivityHelper {

    public static void TextViewVisible(EditText et, TextView tv) {
        et.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
    }

    public static void EditTextVisible(EditText et, TextView tv) {
        et.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
    }

    public static int getInt(EditText et) {
        String text = et.getText().toString();
        if (text.equals("")) {
            return 0;
        } else if (text.length() == 2 && text.charAt(0) == '0') {
            System.out.println("1: " + text);
            return Integer.parseInt(text.charAt(1) + "");
        }
        System.out.println("2: " + text);
        return Integer.parseInt(text);
    }

    public static boolean isZero(EditText et) {
        boolean state = false;
        String text = et.getText().toString();
        if (text.length() == 0
                || text.equals("00")
                || text.equals("0")){
            state = true;
        }
        return state;
    }
}

class TimeHelper {

    final private TextView hoursText;
    final private TextView minutesText;
    final private TextView secondsText;

    public TimeHelper(TextView hoursText, TextView minutesText, TextView secondsText) {
        this.hoursText = hoursText;
        this.minutesText = minutesText;
        this.secondsText = secondsText;
    }

    public boolean minusSecond() {
        boolean isEnd = false;
        int second = Integer.parseInt(secondsText.getText().toString());
        if (second == 0) {
            isEnd = minusMinute();
            secondsText.setText("59");
        } else {
            secondsText.setText(String.valueOf(second - 1));
        }
        return isEnd;
    }

    public boolean minusMinute() {
        boolean isEnd = false;
        int minute = Integer.parseInt(minutesText.getText().toString());
        if (minute == 0) {
            isEnd = minusHour();
            minutesText.setText("59");
        } else {
            minutesText.setText(String.valueOf(minute - 1));
        }
        return isEnd;
    }

    public boolean minusHour() {
        boolean isEnd = false;
        int hour = Integer.parseInt(hoursText.getText().toString());
        if (hour == 0) {
            isEnd = true;
        } else {
            hoursText.setText(String.valueOf(hour - 1));
        }
        return isEnd;
    }
}