package com.danteculaciati.studybuddy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveDao;
import com.danteculaciati.studybuddy.Objectives.ObjectiveDatabase;
import com.danteculaciati.studybuddy.Objectives.ObjectiveType;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;


class TestUtil {
    // Create a basic, trivial objective.
    static Objective createObjective() {
        Objective objective = new Objective();
        objective.setTitle("Test Objective");
        objective.setType(ObjectiveType.OBJECTIVE_DO);
        objective.setAmount(10);
        objective.setStartDate(LocalDate.now());
        objective.setEndDate(LocalDate.now().plusDays(1));

        return objective;
    }
}

@RunWith(AndroidJUnit4.class)
public class ObjectiveDaoInstrumentedTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private Observer<List<Objective>> listObserver;
    private Observer<Objective> objectiveObserver;
    private Objective objective;
    private ObjectiveDao objectiveDao;
    private ObjectiveDatabase db;

    @Before
    public void setup() {
        objective = TestUtil.createObjective();
        objective.setId(1); // id is set to 1 because 0 counts as a null value (to Room).

        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ObjectiveDatabase.class).allowMainThreadQueries().build();
        objectiveDao = db.getObjectiveDao();
    }

    @After
    public void cleanup() {
        db.close();
        listObserver = null;
        objectiveObserver = null;
    }

    @Test
    public void objectiveIsInsertedIntoDatabase() {
        objectiveDao.insertAll(objective);
        listObserver = objectives -> assertThat(objectives.get(0), equalTo(objective));
        objectiveDao.getAll().observeForever(listObserver);
    }

    @Test
    public void objectiveIsActivated() {
        objectiveDao.insertAll(objective);
        listObserver = objectives -> assertThat(objectives.get(0), equalTo(objective));
        objectiveDao.getActive().observeForever(listObserver);
    }

    @Test public void objectiveIsNotActivated() {
        objective.setEndDate(LocalDate.now().minusDays(1));
        objectiveDao.insertAll(objective);

        listObserver = objectives -> assertTrue(objectives.isEmpty());
        objectiveDao.getActive().observeForever(listObserver);
    }

    @Test
    public void objectiveIsUpdated() {
        objectiveDao.insertAll(objective);
        listObserver = objectives -> assertEquals(objectives.get(0).getTitle(), objective.getTitle());
        objectiveDao.getAll().observeForever(listObserver);

        objective.setTitle("New Title");
        listObserver = objectives -> assertEquals(objectives.get(0).getTitle(), objective.getTitle());
        objectiveDao.updateAll(objective);
    }

    @Test
    public void objectiveIsDeleted() {
        objectiveDao.insertAll(objective);
        objectiveDao.deleteAll(objective);
        listObserver = objectives -> assertTrue(objectives.isEmpty());
        objectiveDao.getAll().observeForever(listObserver);
    }

    @Test
    public void objectiveCanBeSearchedById() {
        objective.setId(10);
        objectiveDao.insertAll(objective);

        objectiveObserver = returned_objective -> assertThat(returned_objective, equalTo(objective));
        objectiveDao.findById(10).observeForever(objectiveObserver);
    }

    @Test
    public void objectiveCanBeSearchedByType() {
        objective.setType(ObjectiveType.OBJECTIVE_READ);
        objectiveDao.insertAll(objective);

        objective.setType(ObjectiveType.OBJECTIVE_LISTEN);
        objectiveDao.insertAll(objective);

        listObserver = objectives -> assertThat(objectives.get(0), equalTo(objective));
        objectiveDao.findByType(ObjectiveType.OBJECTIVE_LISTEN).observeForever(listObserver);
    }

    @Test
    public void objectiveIsReplaced() {
        objective.setId(10);
        objectiveDao.insertAll(objective);

        objective.setTitle("New Title");
        objectiveDao.insertAll(objective);

        objectiveObserver = objective -> assertEquals(objective.getTitle(), "New Title");
        objectiveDao.findById(10).observeForever(objectiveObserver);
    }
}