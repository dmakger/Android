package com.example.macclock;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Alarm {

    public String time;
    public boolean stateAlarm;
    public int seconds;
    private MediaPlayer sound;
    private int idSound;

    public Alarm(String time, boolean stateAlarm) {
        this(time, stateAlarm, 0);
    }

    public Alarm(String time, boolean stateAlarm, int seconds) {
        this.time = time;
        this.stateAlarm = stateAlarm;
        this.seconds = seconds;
    }

    public int getIdSound() {
        return idSound;
    }

    public void setSound(Context context, int sound) {
        idSound = sound;
        this.sound = MediaPlayer.create(context, idSound);
    }

    public void soundPlay() {
        sound.start();
        sound.setLooping(true);
    }

    public boolean soundIsPlaying() {
        return sound.isPlaying();
    }

    public void soundStop() {
        sound.setLooping(false);
        sound.stop();
    }

    private int getPartTimeInteger(String pattern, Date currentDate) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        String needPartTimeString = dateFormat.format(currentDate);
        return Integer.parseInt(needPartTimeString);
    }

    private int getDifferenceMinute(int hour1, int minute1, int hour2, int minute2) {
        int differenceHour;
        int differenceMinute;
        System.out.println(hour1 + ":" + minute1 + " ==> " + hour2 + ":" + minute2);
        // Вычислям разницу во времени
        // Если поставленный час больше текущего
        if (hour1 < hour2) {
            differenceHour = hour2 - hour1;
            // Если поставленных минут больше
            if (minute1 <= minute2) {
                differenceMinute = minute2 - minute1;
            }
            // Если текущих минут больше
            else {
                differenceHour -= 1;
                differenceMinute = 60 - minute2 + minute1;
            }
        }
        // Если поставленный час равно текущиму
        else if (hour1 == hour2) {
            differenceHour = hour2 - hour1;
            // Если поставленных минут больше
            if (minute1 < minute2) {
                differenceMinute = minute2 - minute1;
            }
            // Если поставленные минуты равны текущим
            else if (minute2 == minute1) {
                differenceHour = 24;
                differenceMinute = 0;
            }
            // Если текущие минуты больше
            else {
                differenceHour = 23;
                differenceMinute = 60 - minute1 + minute2;
            }
        }
        // Если текущий час больше поставленного
        else {
            differenceHour = 24 - hour1 + hour2;
            // Если поставленных минут больше
            if (minute1 <= minute2) {
                differenceMinute = minute2 - minute1;
            }
            // Если текущих минут больше
            else {
                differenceHour -= 1;
                differenceMinute = 60 - minute1 + minute2;
            }
        }
        System.out.println(differenceHour + ":" + differenceMinute);

        // Возвращаем разницу во времени в минутах
        return (differenceHour*24 + differenceMinute);
    }

    public int getNeedSeconds(String time) {
        String[] timeArray =  time.split(":");
        return getNeedSeconds(timeArray[0], timeArray[1]);
    }

    public int getNeedSeconds(String hour, String minute) {
        int needHour = Integer.parseInt(hour);
        int needMinute = Integer.parseInt(minute);

        Date currentDate = new Date();
        System.out.println(currentDate);
        int currentHour = getPartTimeInteger("HH", currentDate);
        int currentMinute = getPartTimeInteger("mm", currentDate);
        int currentSecond = getPartTimeInteger("ss", currentDate);

        return getDifferenceMinute(
                currentHour, currentMinute, needHour, needMinute)*60 - currentSecond;
    }


    @Override
    public String toString() {
//        return time;
        return "Alarm{" +
                "time='" + time + '\'' +
                ", stateAlarm=" + stateAlarm +
                ", seconds=" + seconds +
                '}';
    }
}

