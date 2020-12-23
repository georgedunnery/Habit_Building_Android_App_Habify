package com.neu.habify.data.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Record;
import com.neu.habify.ui.stats.StatsViewModel;

import java.util.List;

public class StatsRepository extends HabifyRepository {

    private StatsViewModel callback;
    private LiveData<List<Habit>> allActiveHabits;
    private LiveData<List<Badge>> allEarnedBadges;
    private LiveData<List<Record>> allCompletedTasks;
    private LiveData<List<Record>> allPerfectDays;

    public StatsRepository(Application app, StatsViewModel callback) {
        super(app);
        this.callback = callback;
        allActiveHabits = dao.getActiveHabits();
        allEarnedBadges = dao.getAllEarnedBadges(1);
        allCompletedTasks = dao.getAllCompletedTasks();
        allPerfectDays = dao.getAllPerfectDays();
    }

    public LiveData<List<Habit>> getAllActiveHabits() {
        return allActiveHabits;
    }

    public LiveData<List<Badge>> getAllEarnedBadges() { return allEarnedBadges; }

    public LiveData<List<Record>> getAllCompletedTasks() { return allCompletedTasks; }

    public LiveData<List<Record>> getAllPerfectDays() { return allPerfectDays; }
}
