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

    // This method makes sure that only one instance of the database
    // is being worked with at all times.
    static ObjectiveDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ObjectiveDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            ObjectiveDatabase.class, "objectives.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}