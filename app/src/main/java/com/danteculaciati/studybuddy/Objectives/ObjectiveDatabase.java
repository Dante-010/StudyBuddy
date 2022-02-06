package com.danteculaciati.studybuddy.Objectives;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Objective.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ObjectiveDatabase extends RoomDatabase {
    public abstract ObjectiveDao getObjectiveDao();
    private static ObjectiveDatabase INSTANCE;

    static ObjectiveDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ObjectiveDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ObjectiveDatabase.class, "objective_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}