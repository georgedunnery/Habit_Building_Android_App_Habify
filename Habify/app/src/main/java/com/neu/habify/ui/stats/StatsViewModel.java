package com.neu.habify.ui.stats;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Record;
import com.neu.habify.data.room.ProfileRepository;
import com.neu.habify.data.room.StatsRepository;

import java.util.List;

public class StatsViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private LiveData<List<Habit>> allActiveHabits;
    private LiveData<List<Badge>> allEarnedBadges;
    private LiveData<List<Record>> allCompletedTasks;
    private LiveData<List<Record>> allPerfectDays;
    private StatsRepository statsRepo;

    public StatsViewModel(Application app) {
        super(app);
        mText = new MutableLiveData<>();
        mText.setValue("This is the stats fragment");
        statsRepo = new StatsRepository(app, this);
        allActiveHabits = statsRepo.getAllActiveHabits();
        allEarnedBadges = statsRepo.getAllEarnedBadges();
        allCompletedTasks = statsRepo.getAllCompletedTasks();
        allPerfectDays = statsRepo.getAllPerfectDays();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Habit>> getAllActiveHabits() {
        return allActiveHabits;
    }

    public LiveData<List<Badge>> getAllEarnedBadges() {
        return allEarnedBadges;
    }

    public LiveData<List<Record>> getAllCompletedTasks() {
        return allCompletedTasks;
    }

    public LiveData<List<Record>> getAllPerfectDays() {
        return allPerfectDays;
    }
}
