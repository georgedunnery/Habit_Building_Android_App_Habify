package com.neu.habify.data.room;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import com.neu.habify.data.entity.Award;
import com.neu.habify.data.entity.DetailedHabitData;
import com.neu.habify.data.entity.Event;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Location;
import com.neu.habify.data.entity.Record;
import com.neu.habify.ui.dashboard.DashboardViewModel;
import com.neu.habify.utility.BadgeUtils;
import com.neu.habify.utility.EventDateFormatter;

import java.util.Arrays;
import java.util.List;

public class DashboardRepository extends HabifyRepository {

    private final static String DASH_REPO_TAG = "DashboardRepository";
    private LiveData<List<DetailedHabitData>> habits;
    private DashboardViewModel callback;
    private int confirmingHabitId;
    private boolean evaluation;
    private Integer[] habitIdArray;

    public DashboardRepository(Application app, DashboardViewModel callback) {
        super(app);
        this.callback = callback;

        // TODO is this safe to comment out?
        //this.habits = this.dao.getAllHabits();
    }

    public void getLastInsertedRecord(Integer habitId) {
        FragmentActivity activity = this.callback.getFragmentCallback().getActivity();
        new GetLastRecord(dao, this, activity).execute(habitId);
    }

    public LiveData<List<DetailedHabitData>> getHabits() {
        return this.habits;
    }

    public void getTodaysHabits(String dayName) {
        Log.i(DASH_REPO_TAG, "query with day name=" + dayName);
        new GetDayId(dao, this).execute(dayName);
    }

    private void findHabitsForToday(Integer dayId) {
        Log.i(DASH_REPO_TAG, "dayId=" + dayId);
        // TODO UNRESOLVED NULL POINTER EXCEPTION I HAVE NO IDEA WHERE THIS IS COMING FROM
        if (dayId != null) {
            new QueryDashboardHabits(dao, this).execute(dayId);
        }
    }

    private void provideHabitsByIdList(List<Integer> habitIds) {
        // Ids as an array for ellipses style argument
        Integer[] habitIdArray = new Integer[habitIds.size()];
        this.habitIdArray = habitIds.toArray(habitIdArray);
        new DoesEventExist(dao, this).execute(EventDateFormatter.getCurrentDate());
    }

    private void handleDoesEventExist(Integer result) {
        if (result == null) {
            // Create an event for today if there isn't one already
            // Need a better solution but this works for now
            new InsertEventForDetailedCascade(dao, this).execute(new Event(EventDateFormatter.getCurrentDate()));
        }
        else {
            findDetailedHabitsListUsingIds(result);
        }
    }

    private void findEventForDetailedCascade() {
        new QueryEventDetailedCascade(dao, this).execute(EventDateFormatter.getCurrentDate());
    }

    private void findDetailedHabitsListUsingIds(Integer eventId) {
        Integer[] idArray = new Integer[this.habitIdArray.length + 1];
        idArray[0] = eventId;
        // Add the habit ids. The idArray will be one ahead of i due to eventId taking first spot
        for (int i = 0; i < this.habitIdArray.length; i++) {
            idArray[i+1] = this.habitIdArray[i];
        }
        new FindDetailedHabitsByIds(dao, this).execute(idArray);
    }

    private void returnHabitsList(LiveData<List<DetailedHabitData>> habits) {
        this.habits = habits;
        this.callback.setHabits(habits);
    }

    private static class GetDayId extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        GetDayId(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(String... strings) {
            return asyncDao.getDayId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.findHabitsForToday(result);
        }
    }

    private static class QueryDashboardHabits extends AsyncTask<Integer, Void, List<Integer>> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        QueryDashboardHabits(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected List<Integer> doInBackground(Integer... ints) {
            return asyncDao.getTodaysHabits(ints[0]);
        }
        @Override
        protected void onPostExecute(List<Integer> result) {
            delegate.provideHabitsByIdList(result);
        }
    }

    /**
     * The first id is the event id, the rest are habit ids
     */
    private static class FindDetailedHabitsByIds extends AsyncTask<Integer, Void, LiveData<List<DetailedHabitData>>> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        FindDetailedHabitsByIds(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected LiveData<List<DetailedHabitData>> doInBackground(Integer... ids) {
            Log.i(DASH_REPO_TAG, "habit id array length = " + (ids.length - 1));
            Integer[] habitIds = new Integer[ids.length - 1];
            if (ids.length > 1) {
                Log.i(DASH_REPO_TAG, "found a habit and attempting array slice");
                for (int i = 0; i < habitIds.length; i++) {
                    // The input array is one ahead because the eventId occupied index 0 and we want habit ids here
                    int tempId = ids[i+1];
                    habitIds[i] = tempId;
                }
            }
            Log.i(DASH_REPO_TAG, "detailed habits found = "+ habitIds.length);
            return asyncDao.getDetailedDashboardHabits(ids[0], habitIds);

        }
        @Override
        protected void onPostExecute(LiveData<List<DetailedHabitData>> habits) {
            delegate.returnHabitsList(habits);
        }
    }

    private static class DoesEventExist extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        DoesEventExist(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(String... strings) {
            return asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.handleDoesEventExist(result);
        }
    }

    private static class GetLastRecord extends AsyncTask<Integer, Void, Record> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        @SuppressLint("StaticFieldLeak")
        private FragmentActivity callbackActivity;
        private Integer habitId;
        GetLastRecord(HabifyDao dao, DashboardRepository repo, FragmentActivity activity) {
            asyncDao = dao;
            delegate = repo;
            callbackActivity = activity;
        }

        @Override
        protected Record doInBackground(Integer... habitIds) {
            habitId = habitIds[0];
            List<Record> records = asyncDao.getLastRecord(habitId);
            return records.get(0);
        }
        @Override
        protected void onPostExecute(Record record) {
            BadgeUtils.addNewAward(asyncDao, record, callbackActivity);
        }
    }

    private static class InsertEventForDetailedCascade extends AsyncTask<Event, Void, Void> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;

        InsertEventForDetailedCascade(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }

        @Override
        protected Void doInBackground(Event... events) {
            asyncDao.insertEvent(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            delegate.findEventForDetailedCascade();
        }
    }

    private static class QueryEventDetailedCascade extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        QueryEventDetailedCascade(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(String... strings) {
            return asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.findDetailedHabitsListUsingIds(result);
        }
    }





    public void getExpectedCoordinates(int locationId) {
        new QueryLocationCoordinates(dao, this).execute(locationId);
    }

    private void sendExpectedCoordinates(Double expectedLat, Double expectedLon) {
        this.callback.acceptExpectCoordinates(expectedLat, expectedLon);
    }

    private static class QueryLocationCoordinates extends AsyncTask<Integer, Void, Location> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        QueryLocationCoordinates(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Location doInBackground(Integer... ints) {
            return asyncDao.getLocationById(ints[0]);
        }
        @Override
        protected void onPostExecute(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            delegate.sendExpectedCoordinates(latitude, longitude);
        }
    }



    /**
     * Starts a cascade that ultimately inserts a record for the given habit
     * @param habitId
     * @param evaluation
     */
    public void insertRecord(int habitId, boolean evaluation) {
        this.confirmingHabitId = habitId;
        this.evaluation = evaluation;
        new CheckIfEventExists(dao, this).execute(EventDateFormatter.getCurrentDate());
    }

    private void handleEventExistence(Integer eventId) {
        if (eventId == null) {
            new InsertEvent(dao, this).execute(new Event(EventDateFormatter.getCurrentDate()));
        }
        else {
            createRecord(eventId);
        }
    }

    private void findNewEventId() {
        new QueryFindNewEventId(dao, this).execute(EventDateFormatter.getCurrentDate());
    }

    private void createRecord(Integer eventId) {
        String time = EventDateFormatter.getTimeString();
        Log.i(DASH_REPO_TAG, "using time as="+time);
        new InsertRecord(dao, this).execute(new Record(this.confirmingHabitId, eventId, time,this.evaluation));
    }

    private void announceRecordInsertCascadeFinished() {
        this.callback.notifyHabitHasBeenRecorded();
    }

    private static class CheckIfEventExists extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        CheckIfEventExists(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(String... strings) {
            return asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.handleEventExistence(result);
        }
    }

    private static class InsertEvent extends AsyncTask<Event, Void, Void> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        InsertEvent(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(Event... events) {
            asyncDao.insertEvent(events[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void nothing) {
            delegate.findNewEventId();
        }
    }

    private static class QueryFindNewEventId extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        QueryFindNewEventId(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(String... strings) {
            return asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer id) {
            delegate.createRecord(id);
        }
    }

    private static class InsertRecord extends AsyncTask<Record, Void, Void> {
        private HabifyDao asyncDao;
        private DashboardRepository delegate;
        InsertRecord(HabifyDao dao, DashboardRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(Record... records) {
            asyncDao.insertRecord(records[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void nothing) {
            delegate.announceRecordInsertCascadeFinished();
        }
    }

}
