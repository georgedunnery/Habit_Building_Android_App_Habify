package com.neu.habify.data.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.User;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.ui.profile.ProfileViewModel;

import java.util.List;

public class ProfileRepository extends HabifyRepository {

    private static final String PROF_REPO = "ProfileRepository";
    private LiveData<List<Badge>> badges;
    private ProfileViewModel callback;
    private Integer userId;
    private LiveData<List<User>> allUsrs;
    private MutableLiveData<List<User>> searchResults =
            new MutableLiveData<>();

    public ProfileRepository(Application app, ProfileViewModel callback) {
        super(app);
        this.callback = callback;

        allUsrs = dao.getAllUsers();
    }



    private void asyncFinished(List<User> results) {
        searchResults.setValue(results);
    }

    private static class QueryAsyncTask extends
            AsyncTask<String, Void, List<User>> {

        private HabifyDao asyncTaskDao;
        private ProfileRepository delegate = null;

        QueryAsyncTask(HabifyDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<User> doInBackground(final String... params) {
            return asyncTaskDao.getAllNames();
        }

        @Override
        protected void onPostExecute(List<User> result) {
            delegate.asyncFinished(result);
        }
    }

    public void findName() {
        QueryAsyncTask task = new QueryAsyncTask(dao);
        task.delegate = this;
        task.execute();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsrs;
    }

    public MutableLiveData<List<User>> getSearchName() {
        return searchResults;
    }

    public void getEarnedBadges() {
        new QueryUserId(dao, this).execute();
    }

    private void saveUserId(List<Integer> result) {
        if (result.size() > 1) {
            Log.i(PROF_REPO, "Warning: multiple users found");
        }
        this.userId = result.get(0);
        new QueryAllEarnedBadges(dao, this).execute(this.userId);
    }

    private void returnBadgesList(LiveData<List<Badge>> badges) {
        this.badges = badges;
        this.callback.setBadges(this.badges);
    }

    private static class QueryUserId extends AsyncTask<Void, Void, List<Integer>> {
        private HabifyDao asyncDao;
        private ProfileRepository delegate;
        QueryUserId(HabifyDao dao, ProfileRepository repo) {
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


    private static class QueryAllEarnedBadges extends AsyncTask <Integer, Void, LiveData<List<Badge>>> {
        private HabifyDao asyncDao;
        private ProfileRepository delegate;
        QueryAllEarnedBadges(HabifyDao dao, ProfileRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected LiveData<List<Badge>> doInBackground(Integer... ints) {
            return asyncDao.getAllEarnedBadges(ints[0]);
        }
        @Override
        protected void onPostExecute(LiveData<List<Badge>> badges) {
            delegate.returnBadgesList(badges);
        }
    }
}
