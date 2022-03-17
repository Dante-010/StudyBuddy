package com.danteculaciati.studybuddy.Objectives;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "objective_database")
public class Objective {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int amount;
    private String title;
    private ObjectiveType type;
    @ColumnInfo(name="start_date") private LocalDate startDate;
    @ColumnInfo(name="end_date") private LocalDate endDate;

    // Stores whether the objective was completed during that day.
    @ColumnInfo(name="daily_completed") private boolean dailyCompleted = false;
    // Stores number of days without completing daily amount.
    @ColumnInfo(name="missed_days") private int missedDays = 0;

    public Objective() {
        this("", ObjectiveType.OBJECTIVE_DO, 0, LocalDate.now(), LocalDate.now().plusDays(1));
    }

    @Ignore
    public Objective(String title, ObjectiveType type, int amount, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.type = type;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        id = 0;
    }

    // Getters

    public int getId() { return id; }
    public String getTitle(){ return title; }
    public ObjectiveType getType() { return type; }
    public int getAmount() { return amount; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    public boolean isDailyCompleted() { return dailyCompleted; }
    public int getMissedDays() { return missedDays; }

    // Setters

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setType(ObjectiveType type) { this.type = type; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public void setDailyCompleted(boolean completed) { dailyCompleted = completed; }
    public void setMissedDays(int missedDays) { this.missedDays = missedDays; }

    public void addMissedDay() { missedDays += 1; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Objective objective = (Objective) o;
        return id == objective.id && amount == objective.amount && Objects.equals(title, objective.title) && type == objective.type && Objects.equals(startDate, objective.startDate) && Objects.equals(endDate, objective.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, type, amount, startDate, endDate);
    }
}