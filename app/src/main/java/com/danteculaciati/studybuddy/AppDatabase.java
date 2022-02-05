package com.danteculaciati.studybuddy;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.danteculaciati.studybuddy.Objectives.*;

@Database(entities = {Objective.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ObjectiveDao getObjectiveDao();
    // TODO: Add a Repository and refactor this database
}