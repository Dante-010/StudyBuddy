package com.danteculaciati.studybuddy.Objectives;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// This class acts as a wrapper for ObjectiveDao and executes
// database transactions on worker threads.
public class ObjectiveViewModel extends AndroidViewModel {
    private static final String TAG = "ObjectiveViewModel";
    private static final int TIMEOUT_SECONDS = 3;

    // One variable is created for each one of the DAO methods,
    // in order to cache the last query inside the ViewModel.
    private LiveData<List<Objective>> allObjectives, activeObjectives, typeObjectives;
    private LiveData<Objective> idObjective;
    private final ObjectiveDao objectiveDao;
    private final ExecutorService worker;

    public ObjectiveViewModel(Application application) {
        super(application);
        ObjectiveDatabase db = ObjectiveDatabase.getDatabase(application.getApplicationContext());
        objectiveDao = db.getObjectiveDao();
        worker = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onCleared() {
        // Perform worker thread cleanup
        super.onCleared();
        worker.shutdown();
        try {
            if (!worker.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                worker.shutdownNow();
            }
            if (!worker.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                Log.e(TAG, "Worker thread did not terminate");
            }
        } catch (InterruptedException ie) {
            worker.shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }

    public LiveData<List<Objective>> getAll() {
        if (allObjectives == null) {
            worker.execute(() -> allObjectives = objectiveDao.getAll());
        }
        return allObjectives;
    }

    public LiveData<List<Objective>> getActive() {
        if (activeObjectives == null) {
            worker.execute(() -> activeObjectives = objectiveDao.getActive());
        }
        return activeObjectives;
    }

    public LiveData<List<Objective>> findByType(ObjectiveType type) {
        if (typeObjectives == null) {
            worker.execute(() -> typeObjectives = objectiveDao.findByType(type));
        }
        return typeObjectives;
    }

    public LiveData<Objective> findById(int id) {
        if (idObjective == null) {
            worker.execute(() -> idObjective = objectiveDao.findById(id));
        }
        return idObjective;
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
}
