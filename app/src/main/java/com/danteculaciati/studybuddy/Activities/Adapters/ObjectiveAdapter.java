package com.danteculaciati.studybuddy.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.danteculaciati.studybuddy.Activities.MainActivity;
import com.danteculaciati.studybuddy.BroadcastReceivers.AlarmReceiver;
import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;
import com.danteculaciati.studybuddy.R;
import com.danteculaciati.studybuddy.databinding.ListElementObjectiveBinding;

import java.time.LocalDate;
import java.util.List;

public class ObjectiveAdapter extends RecyclerView.Adapter<ObjectiveAdapter.ViewHolder> {
    private List<Objective> objectiveList;
    private final Resources res;
    private final ObjectiveViewModel viewModel;

    public ObjectiveAdapter(Resources res, ObjectiveViewModel viewModel) {
        this.res = res;
        this.viewModel = viewModel;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView counterTextView, titleTextView, referenceTextView;
        private final ImageButton objectiveDoneButton;
        private final ProgressBar progressBar;

        private final Resources res;
        private final ObjectiveViewModel viewModel;

        public ViewHolder(ListElementObjectiveBinding binding, Resources res, ObjectiveViewModel viewModel) {
            super(binding.getRoot());
            counterTextView = binding.counterTextView;
            titleTextView = binding.titleTextView;
            referenceTextView = binding.referenceTextView;
            objectiveDoneButton = binding.objectiveDoneButton;
            progressBar = binding.objectiveProgressBar;

            this.res = res;
            this.viewModel = viewModel;
        }

        @SuppressLint("SetTextI18n")
        void setObjective(Objective objective) {
            LocalDate startDate = objective.getStartDate();
            LocalDate endDate = objective.getEndDate();
            String reference_text;

            titleTextView.setText(objective.getTitle());

            // Set progress bar according to how many days have passed since startDate.
            int totalDays = (int) ((endDate.toEpochDay() - startDate.toEpochDay()) + 1);
            int daysPassed = (int) (LocalDate.now().toEpochDay() - startDate.toEpochDay());

            progressBar.setMin(0);
            progressBar.setMax(totalDays);
            progressBar.setProgress(daysPassed, true);

            // Set daily amount according to total days.
            // (this weird formula is used so that the number always rounds up).
            int dailyAmount = ((objective.getAmount() - 1) /
                    (totalDays - objective.getMissedDays())) + 1;
            // Missed days are taken into account so that for every day you miss, the daily amount
            // increments a bit.
            counterTextView.setText(String.valueOf(dailyAmount));

            // Set reference text depending on objective type.
            switch (objective.getType()) {
                case OBJECTIVE_LISTEN:
                case OBJECTIVE_WATCH:
                    reference_text = res.getQuantityString(R.plurals.hours, dailyAmount);
                    break;

                case OBJECTIVE_READ:
                    reference_text = res.getQuantityString(R.plurals.pages, dailyAmount);
                    break;

                default:
                    reference_text = res.getQuantityString(R.plurals.times, dailyAmount);
            }

            referenceTextView.setText(res.getString(R.string.reference_string, reference_text));

            // If daily objective was completed, set StudyBuddy image
            if (objective.isDailyCompleted()) {
                objectiveDoneButton.setImageResource(R.drawable.ic_study_buddy);
            }

            // Set button listener.
            objectiveDoneButton.setOnClickListener( v -> {
                if (!objective.isDailyCompleted()) {
                    objectiveDoneButton.setImageResource(R.drawable.ic_study_buddy);
                    objective.setDailyCompleted(true);
                    viewModel.updateAll(objective);
                    AlarmReceiver.setAlarms(v.getContext());
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListElementObjectiveBinding binding = ListElementObjectiveBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding, res, viewModel);
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
