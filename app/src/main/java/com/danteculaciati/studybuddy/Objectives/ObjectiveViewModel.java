package com.danteculaciati.studybuddy.Objectives;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

// This class acts as a wrapper for ObjectiveDao
public class ObjectiveViewModel extends AndroidViewModel {

    // One variable is created for each one of the DAO methods,
    // in order to cache the last query inside the ViewModel.
    private LiveData<List<Objective>> allObjectives, activeObjectives, typeObjectives;
    private LiveData<Objective> idObjective;

    private final ObjectiveDao objectiveDao;

    public ObjectiveViewModel(Application application) {
        super(application);
        ObjectiveDatabase db = ObjectiveDatabase.getDatabase(application.getApplicationContext());
        objectiveDao = db.getObjectiveDao();
    }

    public LiveData<List<Objective>> getAll() {
        if (allObjectives == null) {
            allObjectives = objectiveDao.getAll();
        }
        return allObjectives;
    }

    public LiveData<List<Objective>> getActive() {
        if (activeObjectives == null) {
            activeObjectives = objectiveDao.getActive();
        }
        return activeObjectives;
    }

    public LiveData<List<Objective>> findByType(ObjectiveType type) {
        if (typeObjectives == null) {
            typeObjectives = objectiveDao.findByType(type);
        }
        return typeObjectives;
    }

    public LiveData<Objective> findById(int id) {
        if (idObjective == null) {
            idObjective = objectiveDao.findById(id);
        }
        return idObjective;
    }

    public void insertAll(Objective... objectives) {
        objectiveDao.insertAll(objectives);
    }

    public void deleteAll(Objective... objectives) {
        objectiveDao.deleteAll(objectives);
    }

    public void updateAll(Objective... objectives) {
        objectiveDao.updateAll(objectives);
    }
}
