package com.danteculaciati.studybuddy;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveType;
import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;
import com.danteculaciati.studybuddy.databinding.ActivityObjectiveCreationBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;

public class ObjectiveCreationActivity extends AppCompatActivity {
    ObjectiveViewModel objectivesViewModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String DATE_PICKER_TAG = "datePicker";
        objectivesViewModel = new ViewModelProvider(this).get(ObjectiveViewModel.class);

        super.onCreate(savedInstanceState);
        ActivityObjectiveCreationBinding binding = ActivityObjectiveCreationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set toolbar back button listener
        binding.toolbarNewObjective.setNavigationOnClickListener( v -> finish() );

        // Set enum values as spinner items
        String[] objectiveTypes = ObjectiveType.names();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, objectiveTypes);
        binding.spinnerObjectiveType.setAdapter(adapter);
        binding.spinnerObjectiveType.setSelection(adapter.getPosition(ObjectiveType.OBJECTIVE_DO.toString())); // Set default value to "Do"

        // Create date range picker
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String[] startDate = new String[1];
        final String[] endDate = new String[1];

        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .build();

        binding.editTextStartEndDates.setOnClickListener( v -> {
            if (!materialDatePicker.isAdded())
            materialDatePicker.show(getSupportFragmentManager(), DATE_PICKER_TAG);
        });

        materialDatePicker.addOnPositiveButtonClickListener( selection -> {
            startDate[0] = dateFormat.format(selection.first);
            endDate[0] = dateFormat.format(selection.second);
            binding.editTextStartEndDates.setText(startDate[0] + " -> " + endDate[0]);
        });

        // Set done button listener and create objective if possible
        binding.buttonCreateObjective.setOnClickListener( v -> {
            LocalDate start, end;
            String titleStr, typeStr, amountStr;

            titleStr = binding.editTextObjectiveTitle.getText().toString();
            typeStr = binding.spinnerObjectiveType.getSelectedItem().toString();
            amountStr = binding.editTextObjectiveAmount.getText().toString();

            if (titleStr.equals("") || typeStr.equals("") || amountStr.equals("")
                    || startDate[0] == null && endDate[0] == null) {
                Toast.makeText(this, "Missing values", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                start = LocalDate.parse(startDate[0]);
                end = LocalDate.parse(endDate[0]);
            }

            objectivesViewModel.insertAll(new Objective(
                    titleStr,
                    ObjectiveType.getEnum(typeStr),
                    Integer.parseInt(amountStr),
                    start,
                    end
            ));

            finish();
        });
    }
}