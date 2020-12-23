package com.neu.habify.ui.calendar;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neu.habify.data.entity.Schedule;
import com.neu.habify.data.room.ScheduleRepository;

import java.util.List;


public class CalendarViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private ScheduleRepository habits;
    private LiveData<List<Schedule>> schedule;

    public CalendarViewModel(Application app) {
        super(app);
        mText = new MutableLiveData<>();
        habits = new ScheduleRepository(app, this);
        mText.setValue("This is the calendar fragment");
        this.schedule = habits.getSchedule();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Schedule>> getSchedule() {
        return schedule;
    }
}
