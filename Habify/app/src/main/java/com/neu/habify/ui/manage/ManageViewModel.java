package com.neu.habify.ui.manage;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.room.ManageRepository;

import java.util.List;

public class ManageViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private ManageRepository manageRepo;
    private ManageFragment callback;
    private LiveData<List<Habit>> manageHabits;

    public ManageViewModel(Application app) {
        super(app);
        mText = new MutableLiveData<>();
        mText.setValue("This is the manage fragment");
        manageRepo = new ManageRepository(app, this);
        setupManageHabitsList();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Habit>> getManageHabits() {
        return this.manageHabits;
    }

    public void receiveCallback(ManageFragment callback) {
        this.callback = callback;
        setupManageHabitsList();
    }

    private void setupManageHabitsList() {
        this.manageRepo.getManageHabits();
    }

    public void setManageHabitsList(LiveData<List<Habit>> habits) {
        this.manageHabits = habits;
        this.callback.notifyHabitsListReady();
    }
}