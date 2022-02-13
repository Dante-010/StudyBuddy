package com.danteculaciati.studybuddy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.danteculaciati.studybuddy.Objectives.Objective;
import com.danteculaciati.studybuddy.Objectives.ObjectiveType;
import com.danteculaciati.studybuddy.Objectives.ObjectiveViewModel;

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
        objective.setQuantity(10);
        objective.setStartDate(LocalDate.now());
        objective.setEndDate(LocalDate.now().plusDays(1));

        return objective;
    }
}

@RunWith(AndroidJUnit4.class)
public class ObjectivesInstrumentedTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private Observer<List<Objective>> listObserver;
    private Observer<Objective> objectiveObserver;
    
    private ObjectiveViewModel viewModel;
    private Objective objective;

    @Before
    public void setup() {
        objective = TestUtil.createObjective();
        objective.setId(1); // id is set to 1 because 0 counts as a null value (to Room).

        Application application = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        viewModel = new ObjectiveViewModel(application);
    }

    @After
    public void cleanup() {
        listObserver = null;
        objectiveObserver = null;
    }

    @Test
    public void objectiveIsInsertedIntoDatabase() {
        viewModel.insertAll(objective);
        listObserver = objectives -> assertThat(objectives.get(0), equalTo(objective));
        viewModel.getAll().observeForever(listObserver);
    }

    @Test
    public void objectiveIsActivated() {
        viewModel.insertAll(objective);
        listObserver = objectives -> assertThat(objectives.get(0), equalTo(objective));
        viewModel.getActive().observeForever(listObserver);
    }

    @Test public void objectiveIsNotActivated() {
        objective.setEndDate(LocalDate.now().minusDays(1));
        viewModel.insertAll(objective);

        listObserver = objectives -> assertTrue(objectives.isEmpty());
        viewModel.getActive().observeForever(listObserver);
    }

    @Test
    public void objectiveIsUpdated() {
        viewModel.insertAll(objective);
        listObserver = objectives -> assertEquals(objectives.get(0).getTitle(), objective.getTitle());
        viewModel.getAll().observeForever(listObserver);

        objective.setTitle("New Title");
        listObserver = objectives -> assertEquals(objectives.get(0).getTitle(), objective.getTitle());
        viewModel.updateAll(objective);
    }

    @Test
    public void objectiveIsDeleted() {
        viewModel.insertAll(objective);
        viewModel.deleteAll(objective);
        listObserver = objectives -> assertTrue(objectives.isEmpty());
        viewModel.getAll().observeForever(listObserver);
    }

    @Test
    public void objectiveCanBeSearchedById() {
        objective.setId(10);
        viewModel.insertAll(objective);

        objectiveObserver = returned_objective -> assertThat(returned_objective, equalTo(objective));
        viewModel.findById(10).observeForever(objectiveObserver);
    }

    @Test
    public void objectiveCanBeSearchedByType() {
        objective.setType(ObjectiveType.OBJECTIVE_READ);
        viewModel.insertAll(objective);

        objective.setType(ObjectiveType.OBJECTIVE_LISTEN);
        viewModel.insertAll(objective);

        listObserver = objectives -> assertThat(objectives.get(0), equalTo(objective));
        viewModel.findByType(ObjectiveType.OBJECTIVE_LISTEN).observeForever(listObserver);
    }

    @Test
    public void objectiveIsReplaced() {
        objective.setId(10);
        viewModel.insertAll(objective);

        objective.setTitle("New Title");
        viewModel.insertAll(objective);

        objectiveObserver = objective -> assertEquals(objective.getTitle(), "New Title");
        viewModel.findById(10).observeForever(objectiveObserver);
    }
}