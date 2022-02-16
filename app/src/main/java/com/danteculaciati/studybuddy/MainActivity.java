package com.danteculaciati.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ObjectiveViewModel model = new ViewModelProvider(this).get(ObjectiveViewModel.class);
//        // TODO: Add observers to update UI
    }
}