package com.danteculaciati.studybuddy.Activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danteculaciati.studybuddy.Activities.Adapters.ObjectiveAdapter;
import com.danteculaciati.studybuddy.BroadcastReceivers.AlarmReceiver;
import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;
import com.danteculaciati.studybuddy.R;
import com.danteculaciati.studybuddy.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String DAILY_REMINDER_TAG = "DAILY_REMINDER";
    public static final String DAILY_CHECK_TAG = "DAILY_CHECK";

    public static final String CHANNEL_ID = "CHANNEL_DAILY_REMINDER";
    public static final String CHANNEL_DESCRIPTION = "Send daily reminder to complete objectives";
    public static final String CHANNEL_NAME = "Daily Reminder";

    public static final int NOTIFICATION_ID = 1;

    ObjectiveViewModel viewModel;
    LiveData<List<Objective>> activeObjectives, dailyCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initial setup.
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(ObjectiveViewModel.class);

        // Register BroadcastReceivers.
        createNotificationChannel();
        registerReceiver(dailyReminderReceiver, new IntentFilter(DAILY_REMINDER_TAG));
        registerReceiver(dailyCheckReceiver, new IntentFilter(DAILY_CHECK_TAG));

        // Set alarms.
        AlarmReceiver.setAlarms(this);

        // Create recycler view and observer objectives.
        ObjectiveAdapter adapter = new ObjectiveAdapter(this.getResources(), viewModel);
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        activeObjectives = viewModel.getActive();
        dailyCompleted = viewModel.getDailyCompleted();

        if (activeObjectives != null) {
            activeObjectives.observe(this, adapter::setObjectiveList);
        }

        // Set button listeners.
        binding.newObjectiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ObjectiveCreationActivity.class);
            startActivity(intent);
        });

         binding.settingsButton.setOnClickListener(v -> {
             Intent intent = new Intent(this, SettingsActivity.class);
             startActivity(intent);
         });
    }

    // Send notification as a daily reminder.
    BroadcastReceiver dailyReminderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int remainingObjectives = 0;

            if (dailyCompleted != null && dailyCompleted.getValue() != null)
                remainingObjectives = Objects.requireNonNull(dailyCompleted.getValue()).size();

            if (remainingObjectives > 0) {
                Intent activity = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activity, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_study_buddy)
                        .setContentTitle(getString(R.string.daily_reminder_message))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                if (activeObjectives != null) {
                    builder.setContentText(getString(R.string.daily_reminder_description, remainingObjectives));
                }
                else {
                    builder.setContentText(getString(R.string.daily_reminder_description_alt));
                }

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    };

    // Check whether daily objective was completed.
    BroadcastReceiver dailyCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activeObjectives != null)
            for (Objective o : Objects.requireNonNull(activeObjectives.getValue())) {
                if (o.isDailyCompleted())
                    o.setDailyCompleted(false);
                else
                    o.addMissedDay();
                viewModel.updateAll(o);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dailyReminderReceiver);
        unregisterReceiver(dailyCheckReceiver);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}