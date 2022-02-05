package com.danteculaciati.studybuddy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveDao;
import com.danteculaciati.studybuddy.Objectives.ObjectiveType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

class TestUtil {
    // Create a basic, trivial objective.
    static Objective createObjective() {
        Objective objective = new Objective();
        objective.setTitle("Test Objective");
        objective.setType(ObjectiveType.OBJECTIVE_DO);
        objective.setQuantity(10);
        objective.setStartDate(LocalDate.now());
        objective.setEndDate(LocalDate.now().plusDays(1));

        return objective;
    }
}

public class ObjectivesInstrumentedTest {
    private ObjectiveDao objectiveDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        objectiveDao = db.getObjectiveDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // In many of these tests, id is set to 1 because, otherwise, Room automatically sets the ID to 1,
    // and when the objects are compared they do not match (a value of 0 counts as null).

    @Test
    public void objectiveIsInsertedIntoDatabase() {
        Objective objective = TestUtil.createObjective();
        objective.setId(1);
        objectiveDao.insertAll(objective);
        List<Objective> objectives = objectiveDao.getAll();

        assertThat(objectives.get(0), equalTo(objective));
    }

    @Test
    public void objectiveIsActivated() {
        Objective objective = TestUtil.createObjective();
        objective.setId(1);
        objectiveDao.insertAll(objective);
        List<Objective> objectives = objectiveDao.getActive();

        assertThat(objectives.get(0), equalTo(objective));
    }

    @Test public void objectiveIsNotActivated() {
        Objective objective = TestUtil.createObjective();
        objective.setEndDate(LocalDate.now().minusDays(1));
        objectiveDao.insertAll(objective);
        List<Objective> objectives = objectiveDao.getActive();

        assertTrue(objectives.isEmpty());
    }

    @Test
    public void objectiveIsUpdated() {
        Objective objective = TestUtil.createObjective();
        objective.setId(1);
        objectiveDao.insertAll(objective);
        List<Objective> objectives = objectiveDao.getAll();
        assertEquals(objectives.get(0).getTitle(), objective.getTitle());

        objective.setTitle("New Title");
        objectiveDao.updateAll(objective);
        objectives = objectiveDao.getAll();

        assertEquals(objectives.get(0).getTitle(), objective.getTitle());
    }

    @Test
    public void objectiveIsDeleted() {
        Objective objective = TestUtil.createObjective();
        objective.setId(1);
        objectiveDao.insertAll(objective);
        List<Objective> objectives = objectiveDao.getAll();
        assertFalse(objectives.isEmpty());

        objectiveDao.deleteAll(objective);
        objectives = objectiveDao.getAll();

        assertTrue(objectives.isEmpty());
    }

    @Test
    public void objectiveCanBeSearchedById() {
        Objective objective = TestUtil.createObjective();
        objective.setId(10);
        objectiveDao.insertAll(objective);
        Objective returned_objective = objectiveDao.findById(10);

        assertThat(returned_objective, equalTo(objective));
    }

    @Test
    public void objectiveCanBeSearchedByType() {
        Objective objective = TestUtil.createObjective();
        for (int i = 0; i < 3; i++) {
            objective.setType(ObjectiveType.OBJECTIVE_DO);
            objectiveDao.insertAll(objective);
        }
        objective.setType(ObjectiveType.OBJECTIVE_READ);
        objectiveDao.insertAll(objective);
        List<Objective> objectives = objectiveDao.findByType(ObjectiveType.OBJECTIVE_DO);

        assertEquals(objectives.size(), 3);
    }

    @Test
    public void objectiveIsReplacedWhenInsertedWithSameId() {
        Objective objective = TestUtil.createObjective();
        objective.setId(10);
        objectiveDao.insertAll(objective);

        objective.setTitle("New Title");
        objectiveDao.insertAll(objective);

        objective = objectiveDao.findById(10);

        assertEquals(objective.getTitle(), "New Title");
    }
}