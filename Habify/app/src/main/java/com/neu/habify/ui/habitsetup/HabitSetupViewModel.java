package com.neu.habify.ui.habitsetup;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.neu.habify.data.room.HabitSetupRepository;

import java.util.HashMap;

public class HabitSetupViewModel extends AndroidViewModel {

    private HabitSetupRepository habitSetupRepo;

    public HabitSetupViewModel(Application app) {
        super(app);
        habitSetupRepo = new HabitSetupRepository(app);
    }

    public void provideHabitSetupInfo(HashMap<String, Boolean> booleans, HashMap<String, String> strings) {
        this.habitSetupRepo.createHabitRoutine(booleans, strings);
    }

    public void setHabitSetupRepoCallback(HabitSetupFragment callback) {
        habitSetupRepo.setCallback(callback);
    }
}
