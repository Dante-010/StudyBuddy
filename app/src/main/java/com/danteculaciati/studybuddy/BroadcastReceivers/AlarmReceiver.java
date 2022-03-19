package com.danteculaciati.studybuddy.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;

import androidx.preference.PreferenceManager;

import com.danteculaciati.studybuddy.Activities.MainActivity;
import com.danteculaciati.studybuddy.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String INTENT_EXTRA_TAG = "ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
        intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarms(context);
        }
        else {
            String alarmType = intent.getStringExtra(INTENT_EXTRA_TAG);
            if (alarmType != null)
                context.sendBroadcast(new Intent(alarmType));
        }
    }

    public static void setAlarms(Context context) {
        // Set daily reminder.
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timeOfDay = preferences.getString(context.getString(R.string.daily_reminder_time_key), "evening");
        int hour;

        switch (timeOfDay) {
            case ("morning"):
                hour = 10;
                break;
            case ("evening"):
                hour = 21;
                break;
            case ("afternoon"):
            default:
                hour = 16;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmIntent.putExtra(INTENT_EXTRA_TAG, MainActivity.DAILY_REMINDER_TAG);
        manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        // Set daily check.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        alarmIntent.putExtra(INTENT_EXTRA_TAG, MainActivity.DAILY_CHECK_TAG);
        manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}