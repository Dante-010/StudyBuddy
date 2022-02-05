package com.danteculaciati.studybuddy.Objectives;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// TODO: Change Dao so that it delivers LiveData<>

@Dao
public interface ObjectiveDao {
    @Query("SELECT * FROM objectives")
    List<Objective> getAll();

    // For an objective to be 'active', its start date should be before the current day,
    // and its end date should be after the current day.
    @Query("SELECT * FROM objectives " +
            "WHERE end_date >= CAST(strftime('%J','now') - strftime('%J','1970-01-01') AS INTEGER) " +
            "AND start_date <= CAST(strftime('%J','now') - strftime('%J','1970-01-01') AS INTEGER);")
    List<Objective> getActive(); // This query is done this way because dates are stored as days since the unix epoch.

    @Query("SELECT * FROM objectives " +
            "WHERE id = :id")
    Objective findById(int id);

    @Query("SELECT * FROM objectives " +
            "WHERE type = :type")
    List<Objective> findByType(ObjectiveType type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Objective... objectives);

    @Delete
    void deleteAll(Objective... objectives);

    @Update
    void updateAll(Objective... objectives);
}