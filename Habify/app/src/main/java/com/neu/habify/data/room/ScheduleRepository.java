package com.neu.habify.data.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Schedule;
import com.neu.habify.ui.calendar.CalendarViewModel;

import java.util.List;

public class ScheduleRepository extends HabifyRepository {

    private LiveData<List<Schedule>> schedule;
    private CalendarViewModel callback;

    public ScheduleRepository(Application app, CalendarViewModel callback) {
        super(app);
        this.callback = callback;
        schedule = dao.getSchedules();
    }

    public LiveData<List<Schedule>> getSchedule() {
        return schedule;
    }

//    public void setSchedule(LiveData<List<Schedule>> schedules) {
//        this.schedule = schedules;
//    }
//
//    private static class QuerySchedule extends AsyncTask<Void, Void, LiveData<List<Schedule>>> {
//        private HabifyDao asyncDao;
//        private ScheduleRepository delegate;
//
//        QuerySchedule(HabifyDao dao, ScheduleRepository repo) {
//            asyncDao = dao;
//            delegate = repo;
//        }
//        @Override
//        protected LiveData<List<Schedule>> doInBackground(Void... v) {
//            return asyncDao.getSchedules();
//        }
//        @Override
//        protected void onPostExecute(LiveData<List<Schedule>> schedules) {
//            delegate.setSchedule(schedules);
//        }
//    }
}
