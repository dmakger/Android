package com.example.alarmclock;

import android.widget.TextView;

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