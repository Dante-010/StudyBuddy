package com.danteculaciati.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;
import com.danteculaciati.studybuddy.databinding.ActivityMainBinding;

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

        ObjectiveViewModel objectivesViewModel = new ViewModelProvider(this).get(ObjectiveViewModel.class);
        ObjectiveAdapter adapter = new ObjectiveAdapter();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        LiveData<List<Objective>> activeObjectives = objectivesViewModel.getActive();
        if (activeObjectives != null) {
            activeObjectives.observe(this, adapter::setObjectiveList);
        }

        // TODO:
        //  - Adjust RecyclerView bottom padding
        //  - Redesign objective look

        binding.newObjectiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ObjectiveCreationActivity.class);
            startActivity(intent);
        });

         binding.settingsButton.setOnClickListener(v -> {
             Intent intent = new Intent(this, SettingsActivity.class);
             startActivity(intent);
         });
    }
}