package com.danteculaciati.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveType;
import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;
import com.danteculaciati.studybuddy.databinding.ActivityMainBinding;

import java.time.LocalDate;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        ObjectiveViewModel model = new ViewModelProvider(this).get(ObjectiveViewModel.class);
        model.nukeDatabase(); // TODO: Remove this once I'm done with debugging
        ObjectiveAdapter adapter = new ObjectiveAdapter();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        LiveData<List<Objective>> activeObjectives = model.getActive();
        if (activeObjectives != null) {
            activeObjectives.observe(this, adapter::setObjectiveList);
        }

        binding.newObjectiveButton.setOnClickListener(v -> {


            Objective objective = new Objective(
                    "Test",
                    ObjectiveType.OBJECTIVE_DO,
                    192,
                    LocalDate.now().minusDays(1),
                    LocalDate.now().plusDays(1)
            );

            model.insertAll(objective);

            Log.d("OBJECTIVE_BUTTON_LISTENER", "New objective called.");
            if (model.getActive() != null) {
                model.getActive().observe(this, adapter::setObjectiveList);
            }
        });

        binding.settingsButton.setOnClickListener(v -> {
            Log.d("SETTINGS_BUTTON_LISTENER", "Settings button clicked.");
        });
/*
    TODO:
     - Add settings activity
     - Add objective creation activity
*/
    }
}