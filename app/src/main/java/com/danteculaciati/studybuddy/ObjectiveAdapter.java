package com.danteculaciati.studybuddy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.databinding.ListElementObjectiveBinding;

import java.time.LocalDate;
import java.util.List;

public class ObjectiveAdapter extends RecyclerView.Adapter<ObjectiveAdapter.ViewHolder> {
    private List<Objective> objectiveList;

    public ObjectiveAdapter() { }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView counterTextView, titleTextView, startDateTextView, endDateTextView;
        private final ImageButton objectiveDoneButton;
        private final ProgressBar progressBar;

        public ViewHolder(ListElementObjectiveBinding binding) {
            super(binding.getRoot());
            counterTextView = binding.counterTextView;
            titleTextView = binding.titleTextView;
            startDateTextView = binding.startDateTextView;
            endDateTextView = binding.endDateTextView;

            objectiveDoneButton = binding.objectiveDoneButton;
            progressBar = binding.objectiveProgressBar;
        }

        void setObjective(Objective objective) {
            LocalDate startDate = objective.getStartDate();
            LocalDate endDate = objective.getEndDate();

            counterTextView.setText(String.valueOf(objective.getAmount()));
            titleTextView.setText(objective.getTitle());
            startDateTextView.setText(startDate.toString());
            endDateTextView.setText(endDate.toString());

            int totalDays = (int) (endDate.toEpochDay() - startDate.toEpochDay());
            int remainingDays = (int) (endDate.toEpochDay() - LocalDate.now().toEpochDay());

            progressBar.setMax(totalDays);
            progressBar.setProgress(remainingDays, true);

            objectiveDoneButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // TODO: Add logic when objective is completed
            Log.d("OBJECTIVE_ADAPTER", "Done button pressed.");
            objectiveDoneButton.setImageResource(R.drawable.ic_study_buddy);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListElementObjectiveBinding binding = ListElementObjectiveBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setObjective(objectiveList.get(position));
    }

    @Override
    public int getItemCount() {
        return objectiveList == null ? 0 : objectiveList.size();
    }

    public void setObjectiveList(List<Objective> newList) {
        if (objectiveList == null) {
            objectiveList = newList;
            notifyItemRangeInserted(0, newList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() { return objectiveList.size(); }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return objectiveList.get(oldItemPosition).getId() ==
                            newList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Objective newObjective = newList.get(newItemPosition);
                    Objective oldObjective = objectiveList.get(oldItemPosition);
                    return newObjective.equals(oldObjective);
                }
            });
            objectiveList = newList;
            result.dispatchUpdatesTo(this);
        }
    }
}
