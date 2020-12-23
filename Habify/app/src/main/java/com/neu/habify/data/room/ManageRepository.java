package com.neu.habify.data.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.neu.habify.data.entity.Habit;
import com.neu.habify.ui.manage.ManageViewModel;

import java.util.List;

public class ManageRepository extends HabifyRepository {

    private static final String MANAGE_REPO = "ManageRepository";
    private ManageViewModel callback;
    private Integer userId;
    private LiveData<List<Habit>> habits;

    public ManageRepository(Application app, ManageViewModel callback) {
        super(app);
        this.callback = callback;
    }

    public void getManageHabits() {
        new QueryUserId(dao, this).execute();
    }

    private void saveUserId(List<Integer> ids) {
        if (ids.size() > 1) {
            Log.i(MANAGE_REPO, "Warning: multiple users found");
        }
        this.userId = ids.get(0);
        new QueryAllHabitsOrderByActive(dao, this).execute(this.userId);
    }

    private void returnOrderedHabits(LiveData<List<Habit>> habits) {
        this.habits = habits;
        this.callback.setManageHabitsList(this.habits);
    }

    private static class QueryUserId extends AsyncTask<Void, Void, List<Integer>> {
        private HabifyDao asyncDao;
        private ManageRepository delegate;
        QueryUserId(HabifyDao dao, ManageRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected List<Integer> doInBackground(final Void... voids) {
            return asyncDao.getUserId();
        }
        @Override
        protected void onPostExecute(List<Integer> result) {
            delegate.saveUserId(result);
        }
    }

    private static class QueryAllHabitsOrderByActive extends AsyncTask<Integer, Void, LiveData<List<Habit>>> {
        private HabifyDao asyncDao;
        private ManageRepository delegate;
        QueryAllHabitsOrderByActive(HabifyDao dao, ManageRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected LiveData<List<Habit>> doInBackground(Integer... ints) {
            return asyncDao.getAllHabitsOrderByActive(ints[0]);
        }
        @Override
        protected void onPostExecute(LiveData<List<Habit>> habits) {
            delegate.returnOrderedHabits(habits);
        }
    }
}
