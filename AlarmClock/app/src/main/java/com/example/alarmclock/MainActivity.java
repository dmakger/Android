package com.example.alarmclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private AlarmAdapter adapter;
    private List<Alarm> alarmList;
    private MediaPlayer sound;

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

        // Если нажали на пустое пространство, закрываем окно
        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDetailAlarm();
                cancelWrapStopAlarm();
            }
        });

        alarmList = new ArrayList<Alarm>();
        adapter = new AlarmAdapter(this, R.layout.list_item, alarmList);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        sound = MediaPlayer.create(this, R.raw.never_gonna_give_you_up_full);
        runTimer();
    }

    private void runTimer() {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                int count = 0;
                Alarm alarm;
                // Проверяем все будильники
                for (int i = 0; i != alarmList.size(); ++i) {
                    alarm = alarmList.get(i);
                    // Если будильник включен, то
                    if (alarm.stateAlarm) {
                        ++count;
                        // Если время прошло, обновляем будильник и проигрываем песню
                        if (alarm.seconds < 0) {
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
        sound.start();
        sound.setLooping(true);
        shadow.setVisibility(View.VISIBLE);
        wrapStopAlarm.setVisibility(View.VISIBLE);
    }

    private void soundStop() {
        sound.stop();
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
            adapter.add(newAlarm);
            System.out.println(alarmList);
            cancelDetailAlarm();
        }
    }

    public void onClickStopAlarm(View view) {
        soundStop();
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
        if (Integer.parseInt(hour) > 23 || Integer.parseInt(minute) > 59) {
            return false;
        }
        return true;
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