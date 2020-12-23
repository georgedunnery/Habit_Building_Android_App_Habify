package com.neu.habify.data.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.neu.habify.MainActivity;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Day;
import com.neu.habify.data.entity.Event;
import com.neu.habify.data.entity.User;
import com.neu.habify.utility.EventDateFormatter;

import java.util.List;

/**
 * Class to handle first time setup and constant data verification of the database.
 * The following tables will have pre-populated constant data: days, badges
 */
public class DataSetupRepository extends HabifyRepository {

    private static final String DATA_SETUP_REPO = "DataSetupRepo";
    private final int expectedDays = 7;
    private final int expectedBadges = 60;
    private final int minRequiredUsers = 1;
    private MainActivity callback;
    private String firstName;
    private String lastName;
    private String userName;

    public DataSetupRepository(Application app) {
        super(app);
    }

    public void verifyData(MainActivity callback) {
        // This is a callback to the main activity
        // User must perform first time profile setup before the rest of the app is accessible
        this.callback = callback;
        // Launch async tasks to query number of entries
        // Their onPostExecute will then call the verify methods and launch inserts if necessary
        new QueryNumDays(this.dao, this).execute();
        new QueryNumBadges(this.dao, this).execute();
        new QueryNumUsers(this.dao, this).execute();
    }

    protected void verifyDays(Integer numDays) {
        if (numDays == expectedDays) {
            Log.i(DATA_SETUP_REPO, "days table: verified");
        }
        else if (numDays == 0) {
            setupDays();
            Log.i(DATA_SETUP_REPO, "days table: empty -> populated");
        }
        else {
            dao.clearDays();
            setupDays();
            Log.i(DATA_SETUP_REPO, "days table: wrong count -> cleared & repopulated");
        }
    }

    private void setupDays() {
        new InsertDays(this.dao).execute(new Day("Monday"), new Day("Tuesday"),
                new Day("Wednesday"), new Day("Thursday"), new Day("Friday"),
                new Day("Saturday"), new Day("Sunday"));
    }

    protected void verifyBadges(Integer numBadges) {
        if (numBadges == expectedBadges) {
            Log.i(DATA_SETUP_REPO, "badges table: verified");
        }
        else if (numBadges == 0) {
            setupBadges();
            Log.i(DATA_SETUP_REPO, "badges table: empty -> populated");
        }
        else {
            dao.clearBadges();
            setupBadges();
            Log.i(DATA_SETUP_REPO, "badges table: wrong count -> cleared & repopulated");
        }
    }

    private String constructiveRewardMessage(int day){
        return "Congratulations!\n"
               + day + " day streak reached!\n"
               + "Keep up the good work!";
    }

    private void setupBadges() {
        final String incrementalRewardMessage = "Congratulations!\n"
                                              + "You've earned a new badge!\n"
                                              + "Keep up the good work!";
        new InsertBadges(this.dao).execute(
                new Badge(1, incrementalRewardMessage, 10, "trophy_bronze_num_1"),
                new Badge(2, incrementalRewardMessage, 10, "trophy_bronze_num_2"),
                new Badge(3, incrementalRewardMessage, 10, "trophy_bronze_num_3"),
                new Badge(4, incrementalRewardMessage, 10, "trophy_bronze_num_4"),
                new Badge(5, incrementalRewardMessage, 10, "trophy_bronze_num_5"),
                new Badge(6, constructiveRewardMessage(6), 20, "trophy_bronze_star_1"),
                new Badge(7, incrementalRewardMessage, 20, "trophy_bronze_star_2"),
                new Badge(8, incrementalRewardMessage, 20, "trophy_bronze_star_3"),
                new Badge(9, incrementalRewardMessage, 20, "trophy_bronze_star_4"),
                new Badge(10, incrementalRewardMessage, 20, "trophy_bronze_star_5"),
                new Badge(11, constructiveRewardMessage(11), 40, "trophy_bronze_diamond_1"),
                new Badge(12, incrementalRewardMessage, 40, "trophy_bronze_diamond_2"),
                new Badge(13, incrementalRewardMessage, 40, "trophy_bronze_diamond_3"),
                new Badge(14, incrementalRewardMessage, 40, "trophy_bronze_diamond_4"),
                new Badge(15, incrementalRewardMessage, 40, "trophy_bronze_diamond_5"),

                new Badge(16, constructiveRewardMessage(16), 80, "trophy_silver_num_1"),
                new Badge(17, incrementalRewardMessage, 80, "trophy_silver_num_2"),
                new Badge(18, incrementalRewardMessage, 80, "trophy_silver_num_3"),
                new Badge(19, incrementalRewardMessage, 80, "trophy_silver_num_4"),
                new Badge(20, incrementalRewardMessage, 80, "trophy_silver_num_5"),
                new Badge(21, constructiveRewardMessage(21), 160, "trophy_silver_star_1"),
                new Badge(22, incrementalRewardMessage, 160, "trophy_silver_star_2"),
                new Badge(23, incrementalRewardMessage, 160, "trophy_silver_star_3"),
                new Badge(24, incrementalRewardMessage, 160, "trophy_silver_star_4"),
                new Badge(25, incrementalRewardMessage, 160, "trophy_silver_star_5"),
                new Badge(26, constructiveRewardMessage(26), 320, "trophy_silver_diamond_1"),
                new Badge(27, incrementalRewardMessage, 320, "trophy_silver_diamond_2"),
                new Badge(28, incrementalRewardMessage, 320, "trophy_silver_diamond_3"),
                new Badge(29, incrementalRewardMessage, 320, "trophy_silver_diamond_4"),
                new Badge(30, incrementalRewardMessage, 320, "trophy_silver_diamond_5"),

                new Badge(31, constructiveRewardMessage(31), 640, "trophy_gold_num_1"),
                new Badge(32, incrementalRewardMessage, 640, "trophy_gold_num_2"),
                new Badge(33, incrementalRewardMessage, 640, "trophy_gold_num_3"),
                new Badge(34, incrementalRewardMessage, 640, "trophy_gold_num_4"),
                new Badge(35, incrementalRewardMessage, 640, "trophy_gold_num_5"),
                new Badge(36, constructiveRewardMessage(36), 1280, "trophy_gold_star_1"),
                new Badge(37, incrementalRewardMessage, 1280, "trophy_gold_star_2"),
                new Badge(38, incrementalRewardMessage, 1280, "trophy_gold_star_3"),
                new Badge(39, incrementalRewardMessage, 1280, "trophy_gold_star_4"),
                new Badge(40, incrementalRewardMessage, 1280, "trophy_gold_star_5"),
                new Badge(41, constructiveRewardMessage(41), 2560, "trophy_gold_diamond_1"),
                new Badge(42, incrementalRewardMessage, 2560, "trophy_gold_diamond_2"),
                new Badge(43, incrementalRewardMessage, 2560, "trophy_gold_diamond_3"),
                new Badge(44, incrementalRewardMessage, 2560, "trophy_gold_diamond_4"),
                new Badge(45, incrementalRewardMessage, 2560, "trophy_gold_diamond_5"),

                new Badge(46, constructiveRewardMessage(46), 5120, "trophy_platinum_num_1"),
                new Badge(47, incrementalRewardMessage, 5120, "trophy_platinum_num_2"),
                new Badge(48, incrementalRewardMessage, 5120, "trophy_platinum_num_3"),
                new Badge(49, incrementalRewardMessage, 5120, "trophy_platinum_num_4"),
                new Badge(50, incrementalRewardMessage, 5120, "trophy_platinum_num_5"),
                new Badge(51, constructiveRewardMessage(51), 10240, "trophy_platinum_star_1"),
                new Badge(52, incrementalRewardMessage, 10240, "trophy_platinum_star_2"),
                new Badge(53, incrementalRewardMessage, 10240, "trophy_platinum_star_3"),
                new Badge(54, incrementalRewardMessage, 10240, "trophy_platinum_star_4"),
                new Badge(55, incrementalRewardMessage, 10240, "trophy_platinum_star_5"),
                new Badge(56, constructiveRewardMessage(56), 20480, "trophy_platinum_diamond_1"),
                new Badge(57, incrementalRewardMessage, 20480, "trophy_platinum_diamond_2"),
                new Badge(58, incrementalRewardMessage, 20480, "trophy_platinum_diamond_3"),
                new Badge(59, incrementalRewardMessage, 20480, "trophy_platinum_diamond_4"),
                new Badge(60, incrementalRewardMessage, 20480, "trophy_platinum_diamond_5")
        );
    }

    /**
     * This method will launch a cascade of AsyncTasks on first time profile setup. See setupUser()
     * documentation.
     * @param result
     */
    private void verifyUsers(int result) {
        // If there is a user profile, we don't need to do anything
        if (result >= minRequiredUsers) {
            Log.i(DATA_SETUP_REPO, "users table: " + result + " users exist");
        }
        // If there is no user profile, we have to force the user to make one (first time setup)
        else {
            Log.i(DATA_SETUP_REPO, "users table: no user exists -> initiating profile setup");
            this.callback.setupUserDialog();
            // After this, the setupUser() method is called by the MainActivity with gathered info
        }
    }

    /**
     * Launches a cascade to enforce insert order & ensure validity of foreign key. The overall
     * result of calling this method is the insertion of a user profile entry.
     *
     * Save user profile data in private fields in this class, then call createEvent()
     * Insert an event, which then queries its id in onPostExecute with getEventId()
     * Query event id, which then provides the id to createUser() in onPostExecute
     * Insert the user entry, with the event id as a foreign key
     *
     * @param first
     * @param last
     * @param username
     */
    public void setupUser(String first, String last, String username) {
        Log.i(DATA_SETUP_REPO, "user setup: starting cascade...");
        this.firstName = first;
        this.lastName = last;
        this.userName = username;
        Log.i(DATA_SETUP_REPO, "user setup: received first=" + this.firstName + ", last=" +
                this.lastName + ", username=" + this.userName);
        createEvent();
    }

    private void createEvent() {
        Event created = new Event(EventDateFormatter.getCurrentDate());
        new InsertEvent(dao, this).execute(created);
    }

    private void getEventId(String date) {
        new QueryEventId(dao, this).execute(date);
        Log.i(DATA_SETUP_REPO, "user setup: event created with date=" + date);
    }

    private void createUser(int eventId) {
        Log.i(DATA_SETUP_REPO, "user setup: event_id foreign key value=" + eventId);
        User user = new User(firstName, lastName, userName, eventId);
        new InsertUser(dao, this).execute(user);
        Log.i(DATA_SETUP_REPO, "user setup: cascade finished");
    }

    private static class QueryNumDays extends AsyncTask<Void, Void, Integer> {
        private HabifyDao asyncDao;
        private DataSetupRepository delegate;
        QueryNumDays(HabifyDao dao, DataSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final Void... voids) {
            return this.asyncDao.numberEntriesInDays();
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.verifyDays(result);
        }
    }

    private static class QueryNumBadges extends AsyncTask<Void, Void, Integer> {
        private HabifyDao asyncDao;
        private DataSetupRepository delegate;
        QueryNumBadges(HabifyDao dao, DataSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final Void... voids) {
            return this.asyncDao.numberEntriesInBadges();
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.verifyBadges(result);
        }
    }

    private static class QueryNumUsers extends AsyncTask<Void, Void, Integer> {
        private HabifyDao asyncDao;
        private DataSetupRepository delegate;
        QueryNumUsers(HabifyDao dao, DataSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(Void... voids) {
            return this.asyncDao.numberEntriesInUsers();
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.verifyUsers(result);
        }
    }

    private static class InsertDays extends AsyncTask<Day, Void, Void> {
        private HabifyDao asyncDao;
        InsertDays(HabifyDao dao) {
            asyncDao = dao;
        }
        @Override
        protected Void doInBackground(final Day... days) {
            for (Day d: days) {
                this.asyncDao.insertDay(d);
            }
            return null;
        }
    }

    private static class InsertBadges extends AsyncTask<Badge, Void, Void> {
        private HabifyDao asyncDao;
        InsertBadges(HabifyDao dao) {
            asyncDao = dao;
        }
        @Override
        protected Void doInBackground(final Badge... badges) {
            for (Badge b: badges) {
                this.asyncDao.insertBadge(b);
            }
            return null;
        }
    }

    private static class InsertEvent extends AsyncTask<Event, Void, String> {
        private HabifyDao asyncDao;
        private DataSetupRepository delegate;
        InsertEvent(HabifyDao dao, DataSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected String doInBackground(final Event... events) {
            this.asyncDao.insertEvent(events[0]);
            return events[0].getDate();
        }
        @Override
        protected void onPostExecute(String date) {
            delegate.getEventId(date);
        }
    }

    private static class QueryEventId extends AsyncTask <String, Void, Integer> {
        private HabifyDao asyncDao;
        private DataSetupRepository delegate;
        QueryEventId(HabifyDao dao, DataSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final String... strings) {
            // We pass in one string, the date
            return this.asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer eventId) {
            delegate.createUser(eventId);
        }
    }

    private static class InsertUser extends AsyncTask<User, Void, Void> {
        private HabifyDao asyncDao;
        private DataSetupRepository delegate;
        InsertUser(HabifyDao dao, DataSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(final User... users) {
            this.asyncDao.insertUser(users[0]);
            return null;
        }
    }
}
