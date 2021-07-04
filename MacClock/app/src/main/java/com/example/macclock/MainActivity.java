package com.example.macclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Button addAlarm;
    private RelativeLayout shadow;
    private LinearLayout alarmDetail;
    private EditText hours;
    private EditText minutes;
    private Button cancel;
    private Button saveAlarm;
    private RecyclerView recycler;
    private LinearLayout wrapStopAlarm;

    private int posSound;
    private List<Alarm> alarmList;
    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAlarm = findViewById(R.id.add_alarm);
        shadow = findViewById(R.id.shadow);
        alarmDetail = findViewById(R.id.alarm_detail);
        hours = findViewById(R.id.hours);
        minutes = findViewById(R.id.minutes);
        cancel = findViewById(R.id.cancel);
        saveAlarm = findViewById(R.id.save);
        recycler = findViewById(R.id.list_alarm);
        wrapStopAlarm = findViewById(R.id.wrap_stop_alarm);

        posSound = -1;
        alarmList = new ArrayList<Alarm>();

        if (savedInstanceState != null) {
            alarmList = getAlarmArrayList(savedInstanceState, "alarmList");
            posSound = savedInstanceState.getInt("posSound");
            if (posSound != -1) {
                shadow.setVisibility(View.VISIBLE);
                wrapStopAlarm.setVisibility(View.VISIBLE);
            }
        }
        adapter = new AlarmAdapter(this, R.layout.list_item, alarmList);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.alarm);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.alarm:
                        return true;

                    case R.id.timer:
                        startActivity(new Intent(getApplicationContext(), Timer.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.stopwatch:
                        startActivity(new Intent(getApplicationContext(), Stopwatch.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

        if (savedInstanceState == null) {
            runTimer();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        putAlarmArrayList(outState, "alarmList", alarmList);
        outState.putInt("posSound", posSound);
        System.out.println("!!!onSaveInstanceState!!!");
    }

    public ArrayList<Alarm> getAlarmArrayList(Bundle savedInstanceState, String key) {
        String[] timeArray = savedInstanceState.getStringArray(key + "Times");
        boolean[] stateAlarmArray = savedInstanceState.getBooleanArray(key + "StateAlarms");
        int[] secondsArray = savedInstanceState.getIntArray(key + "Seconds");
        int[] idSoundArray = savedInstanceState.getIntArray(key + "idSounds");

        ArrayList<Alarm> alarmArrayList = new ArrayList<>(timeArray.length);
        for (int i = 0; i != timeArray.length; ++i) {
            Alarm newAlarm = new Alarm(timeArray[i], stateAlarmArray[i]);
            newAlarm.seconds = secondsArray[i];
            newAlarm.setSound(this, idSoundArray[i]);
            alarmArrayList.add(newAlarm);
        }

        return alarmArrayList;
    }

    public void putAlarmArrayList(@NonNull Bundle outState, String key, List<Alarm> alarmArray) {
        String[] timeArray = new String[alarmArray.size()];
        boolean[] stateAlarmArray = new boolean[alarmArray.size()];
        int[] secondsArray = new int[alarmArray.size()];
        int[] idSoundArray = new int[alarmArray.size()];
        for (int i = 0; i != alarmArray.size(); ++i) {
            Alarm alarm = alarmArray.get(i);
            timeArray[i] = alarm.time;
            stateAlarmArray[i] = alarm.stateAlarm;
            secondsArray[i] = alarm.seconds;
            idSoundArray[i] = alarm.getIdSound();
        }

        outState.putStringArray(key + "Times", timeArray);
        outState.putBooleanArray(key + "StateAlarms", stateAlarmArray);
        outState.putIntArray(key + "Seconds", secondsArray);
        outState.putIntArray(key + "idSounds", idSoundArray);
    }

    private void runTimer() {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                Alarm alarm;
                int count = 0;
                // Проверяем все будильники
                for (int i = 0; i != alarmList.size(); ++i) {
                    count += 1;
                    alarm = alarmList.get(i);
                    // Если будильник включен, то
                    if (alarm.stateAlarm) {
//                        System.out.println(alarm.seconds);
                        // Если время прошло, обновляем будильник и проигрываем песню
                        if (alarm.seconds == 0) {
                            // Если будильник уже играет, то выключаем прошлый и включаем новый
                            if (posSound != -1){
                                soundStop();
                            }
                            System.out.println("I am PLAYING");
                            posSound = i;
                            updateAlarm(i);
                            soundPlay();
                        }
                        // Иначе отнимаем секунду
                        else {
                            --alarmList.get(i).seconds;
                        }
                    }
                }
            }
        });
    }

    private void updateAlarm(int position) {
        // Set 24 hours ==> 86_400 seconds
        alarmList.get(position).seconds = 86_400;
    }

    private void soundPlay() {
        alarmList.get(posSound).soundPlay();
        shadow.setVisibility(View.VISIBLE);
        wrapStopAlarm.setVisibility(View.VISIBLE);
    }

    private void soundStop() {
        alarmList.get(posSound).soundStop();
        posSound = -1;
    }

    public void onClickAdd(View view) {
        shadow.setVisibility(View.VISIBLE);
        alarmDetail.setVisibility(View.VISIBLE);
    }

    public void onClickCancel(View view) {
        cancelDetailAlarm();
    }

    public void onClickSave(View view) {
        String hour = getHour(hours);
        String minute = getMinute(minutes);
        if (isSuits(hour, minute)) {
            String time = hour + ":" + minute;
            Alarm newAlarm = new Alarm(time, true);
            newAlarm.seconds = newAlarm.getNeedSeconds(hour, minute);
//            newAlarm.seconds = 3;
            newAlarm.setSound(this, R.raw.never_gonna_give_you_up_full);
            adapter.add(newAlarm);
            System.out.println(alarmList);
            cancelDetailAlarm();
        }
    }

    public void onClickStopAlarm(View view) {
        cancelWrapStopAlarm();
    }

    public static String getHour(EditText hour) {
        String text = hour.getText().toString();
        if (hour.length() == 0) {
            text = "0";
        }
        return text;
    }

    public static String getMinute(EditText minute) {
        String text = minute.getText().toString();
        if (text.length() == 1) {
            text = "0" + text;
        } else if (text.length() == 0) {
            text = "00";
        }
        return text;
    }

    public static boolean isSuits(String hour, String minute) {
        return !(Integer.parseInt(hour) > 23 || Integer.parseInt(minute) > 59);
    }

    private void cancelDetailAlarm() {
        shadow.setVisibility(View.GONE);
        alarmDetail.setVisibility(View.GONE);
        hours.setText("");
        minutes.setText("");
    }

    private void cancelWrapStopAlarm() {
        shadow.setVisibility(View.GONE);
        wrapStopAlarm.setVisibility(View.GONE);
        soundStop();
    }
}