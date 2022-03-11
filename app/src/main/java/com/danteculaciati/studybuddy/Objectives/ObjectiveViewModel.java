package com.danteculaciati.studybuddy.Objectives;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// This class acts as a wrapper for ObjectiveDao
public class ObjectiveViewModel extends AndroidViewModel {

    private final ObjectiveDatabase db;
    private final ObjectiveDao objectiveDao;
    private final ExecutorService worker;

    public ObjectiveViewModel(Application application) {
        super(application);
        db = ObjectiveDatabase.getDatabase(application.getApplicationContext());
        objectiveDao = db.getObjectiveDao();
        worker = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Objective>> getAll() {
        return objectiveDao.getAll();
    }

    public LiveData<List<Objective>> getActive() {
        return objectiveDao.getActive();
    }

    public LiveData<List<Objective>> findByType(ObjectiveType type) {
        return objectiveDao.findByType(type);
    }

    public LiveData<Objective> findById(int id) {
        return objectiveDao.findById(id);
    }

    public void insertAll(Objective... objectives) {
        worker.execute(() -> objectiveDao.insertAll(objectives));
    }

    public void deleteAll(Objective... objectives) {
        worker.execute(() -> objectiveDao.deleteAll(objectives));
    }

    public void updateAll(Objective... objectives) {
        worker.execute(() -> objectiveDao.updateAll(objectives));
    }

    // Use with extreme caution
    public void nukeDatabase() {
        worker.execute(db::clearAllTables);
    }
}
