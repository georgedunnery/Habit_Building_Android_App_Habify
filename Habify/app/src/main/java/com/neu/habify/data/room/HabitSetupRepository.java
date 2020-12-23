package com.neu.habify.data.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.neu.habify.data.entity.Day;
import com.neu.habify.data.entity.Event;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Location;
import com.neu.habify.data.entity.Schedule;
import com.neu.habify.ui.habitsetup.HabitSetupFragment;
import com.neu.habify.utility.EventDateFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HabitSetupRepository extends HabifyRepository {

    private final String HABIT_SETUP = "HabitSetupRepo";
    private HabitSetupFragment callback;
    private HashMap<String, Boolean> habitBooleans;
    private HashMap<String, String> habitStrings;
    private String date;
    private int userId;
    private int eventId;
    private int habitId;
    private Integer locationId = null;
    private double latitude = 0;
    private double longitude = 0;
    private LocatorTask mLocatorTask;
    private GeocodeParameters mGeocodeParameters;

    public HabitSetupRepository(Application app) {
        super(app);
    }

    public void setCallback(HabitSetupFragment callback) {
        // Keep a reference to the fragment so we can post a toast telling the user the habit has been added!
        this.callback = callback;
    }

    /**
     * This method will launch a cascade of AsyncTasks to enforce insert ordering and the validity
     * of foreign keys. The overall result will be the addition of a new habit, and the addition of
     * a new event if no event for today's date exists yet / new location if GPS is enabled and
     * the address is new.
     *
     * Saves booleans, strings -> verifyEvent()
     * Checks if event exists -> if not insert & query id, if it does just query id
     * Save the event_id -> get the user_id next with findUserId()
     * Save the user_id -> setup the location now (if using GPS)
     *
     * Convert the address to gps coordinates -> query if the location already exists in table
     *   If it does, save its id and continue
     *   If not, insert it, query again to get its id, save the id and move on
     *
     * Now ready to insert the habit with insertNewHabit()
     * Insert the habit with the validated FKs -> now queryHabitId()
     * Save habit_id -> now get a list of Day objects with getDaysTable()
     * Save the list of days objects -> now generateSchedule()
     * Run inserts of habit_id + day_id to setup the schedule -> DONE.
     *
     * @param booleans
     * @param strings
     */
    public void createHabitRoutine(HashMap<String, Boolean> booleans, HashMap<String, String> strings) {
        // Save the gathered data for later use
        this.habitBooleans = booleans;
        this.habitStrings = strings;
        // TODO add + "1" to force the insertion of a new event path
        this.date = EventDateFormatter.getCurrentDate();
        Log.i(HABIT_SETUP, " --- Launching Habit Setup Cascade ---");
        Log.i(HABIT_SETUP, "event: using date: " + date);
        verifyEvent(this.date);
    }

    private void verifyEvent(String date) {
        new QueryEventExists(dao, this).execute(date);
    }

    private void handleEventVerification(Integer eventId) {
        if (eventId == null) {
            // Insert, then query id before continuing from the inserts onPostExecute
            // From here, we go to findEventId() -> setEventId() -> findUserId()
            Log.i(HABIT_SETUP, "event: insert new entry");
            new InsertEvent(dao, this).execute(new Event(this.date));
        }
        else {
            // Just save the id and continue
            this.eventId = eventId;
            Log.i(HABIT_SETUP, "event: existing entry was found with id=" + this.eventId);
            findUserId();
        }
    }

    private void findEventId() {
        new QueryEventId(dao, this).execute(this.date);
    }

    private void setEventId(Integer id) {
        this.eventId = id;
        Log.i(HABIT_SETUP, "event: new event id=" + this.eventId);
        findUserId();
    }

    private void findUserId() {
        new QueryUserId(dao, this).execute();
    }

    private void saveUserId(List<Integer> ids) {
        if (ids.size() > 1) {
            Log.i(HABIT_SETUP, "WARNING: multiple users found");
        }
        this.userId = ids.get(0);
        Log.i(HABIT_SETUP, "user: user id=" + this.userId);
        considerLocation();
    }

    private void considerLocation() {
        // Always create a location entry, even if gps not enabled, to save name and address data
        Log.i(HABIT_SETUP, "location: initiating location checks");
        checkIfLocationExists();
    }

    private void checkIfLocationExists() {
        final String address = this.habitStrings.get("Location Address");

        // TODO *ONLY* set the latitude and longitude right here, according to the address using api conversion
        // (do not create a location object, that is already handled, I just need these two values set)
        if (address != null && address.length() > 0) {
            String locatorService = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
            mLocatorTask = new LocatorTask(locatorService);
            mGeocodeParameters = new GeocodeParameters();
            mGeocodeParameters.getResultAttributeNames().add("*");
            mGeocodeParameters.setMaxResults(1);

            final ListenableFuture<List<GeocodeResult>> geocodeFuture = mLocatorTask.geocodeAsync(address, mGeocodeParameters);

            geocodeFuture.addDoneListener(new Runnable() {

                @Override
                public void run() {
                    try {
                        List<GeocodeResult> geocodeResults = geocodeFuture.get();
                        if (geocodeResults.size() > 0) {

                            Double lat = Math.round(geocodeResults.get(0).getDisplayLocation().getY()*1000000d)/1000000d;
                            Double lng = Math.round(geocodeResults.get(0).getDisplayLocation().getX()*1000000d)/1000000d;
                            // Enforce timing constraint
                            sendQueryForLocationAfterCoordinatesAreHandled(lat, lng);
                        }
                        else {
                            // How should we handle this? Perhaps match by address string so we can allow lat/lng to be nullable
                            // (currently my logic would expect the lat/lng to be unique)
                            Log.d(HABIT_SETUP,"No Coordinates Found: " + address);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        // ... determine how you want to handle an error
                        // Probably I will modify my logic so we can have lat/lng be nullable and just pass nulls along
                        Log.d(HABIT_SETUP,"AddressError" + e.getMessage());

                    }
                    geocodeFuture.removeDoneListener(this); // Done searching, remove the listener.
                }
            });
        }
    }

    private void sendQueryForLocationAfterCoordinatesAreHandled(Double lat, Double lng) {
        this.latitude = lat;
        this.longitude = lng;
        Log.i(HABIT_SETUP, "new location coords: lat=" + this.latitude + ", lon=" + this.longitude);
        new QueryLocationExists(dao, this).execute(this.latitude, this.longitude);
    }

    private void handleLocationVerification(Integer id) {
        if (id == null) {
            // InsertLocation task -> findLocationId() -> setLocationId() -> insertNewHabit()
            Log.i(HABIT_SETUP, "location: new location entry");
            Location location = new Location(this.habitStrings.get("Location Name"), this.habitStrings.get("Location Address"), this.latitude, this.longitude);
            new InsertLocation(dao, this).execute(location);
        }
        else {
            this.locationId = id;
            Log.i(HABIT_SETUP, "location: existing location entry with id=" + this.locationId);
            insertNewHabit();
        }
    }

    private void findLocationId() {
        new QueryLocationId(dao, this).execute(this.latitude, this.longitude);
    }

    private void setLocationId(Integer id) {
        this.locationId = id;
        Log.i(HABIT_SETUP, "location: new location entry added with id=" + this.locationId);
        insertNewHabit();
    }

    private void insertNewHabit() {
        // Have user id
        String habitName = this.habitStrings.get("Habit Name");
        // Have location id
        boolean gpsToggle = this.habitBooleans.get("GPS");
        String startTime = this.habitStrings.get("Start Time");
        String endTime = this.habitStrings.get("End Time");
        String boundString = this.habitStrings.get("Bound");
        Integer bound = null;
        if (!boundString.equals("")) {
            bound = Integer.parseInt(boundString);
        }

        Log.i(HABIT_SETUP, "user id = " + this.userId);
        Log.i(HABIT_SETUP, "habit name = " + habitName);
        Log.i(HABIT_SETUP, "location id = " + this.locationId);
        Log.i(HABIT_SETUP, "gps toggle = " + gpsToggle);
        Log.i(HABIT_SETUP, "event id = " + this.eventId);
        Log.i(HABIT_SETUP, "start time = " + startTime);
        Log.i(HABIT_SETUP, "end time = " + endTime);
        Log.i(HABIT_SETUP, "bound = " + bound);


        Habit habit = new Habit(this.userId, habitName, this.locationId, gpsToggle, this.eventId, startTime, endTime, bound);
        Log.i(HABIT_SETUP, "habit: inserting new habit");
        new InsertHabit(dao, this).execute(habit);
    }

    private void getHabitId() {
        new QueryHabitId(dao, this).execute(this.habitStrings.get("Habit Name"));
    }

    private void getDaysTable(Integer habitId) {
        this.habitId = habitId;
        Log.i(HABIT_SETUP, "habit: habit_id=" + this.habitId);
        new QueryAllDays(dao, this).execute();
    }

    private void generateScheduleEntries(List<Day> days) {
        HashMap<String, Integer> daysMap = new HashMap<String, Integer>();
        // Run them through a hashmap for easier lookups
        for (Day d: days) {
            daysMap.put(d.getName(), d.getId());
        }
        // TODO this isn't pretty but it works. A bunch of stuff would need to be refactored to change this lol..
        List<String> selectedDays = new ArrayList<>();
        String logged = "";
        if (this.habitBooleans.get("Monday")) {
            selectedDays.add("Monday");
            logged += "Monday ";
        }
        if (this.habitBooleans.get("Tuesday")) {
            selectedDays.add("Tuesday");
            logged += "Tuesday ";
        }
        if (this.habitBooleans.get("Wednesday")) {
            selectedDays.add("Wednesday");
            logged += "Wednesday ";
        }
        if (this.habitBooleans.get("Thursday")) {
            selectedDays.add("Thursday");
            logged += "Thursday ";
        }
        if (this.habitBooleans.get("Friday")) {
            selectedDays.add("Friday");
            logged += "Friday ";
        }
        if (this.habitBooleans.get("Saturday")) {
            selectedDays.add("Saturday");
            logged += "Saturday ";
        }
        if (this.habitBooleans.get("Sunday")) {
            selectedDays.add("Sunday");
            logged += "Sunday ";
        }
        Log.i(HABIT_SETUP, "schedule: selected days=" + logged);
        // Compare the list of string to hashmap of days to create the generated list of schedule objects
        List<Schedule> schedules = new ArrayList<>();
        for (String s: selectedDays) {
            Integer dayForeignKey = daysMap.get(s);
            schedules.add(new Schedule(this.habitId, dayForeignKey));
        }
        // Convert to an array for the ellipses style argument required by the AsyncTask
        Schedule[] scheduleArray = new Schedule[schedules.size()];
        scheduleArray = schedules.toArray(scheduleArray);
        new InsertSchedule(dao, this).execute(scheduleArray);
    }

    private void insertHabitCascadeFinishedLogger(){
        Log.i(HABIT_SETUP, "IMPORTANT: NEED TO GENERATE SCHEDULE. LOCATION FK SHOULDN'T BE NULLABLE, BUT LAT/LONG COULD BE TO IMPLY USE GPS OR NOT");
        Log.i(HABIT_SETUP, " --- Habit Setup Cascade Finished ---");
        this.callback.announceHabitCreated();
    }

    private static class QueryEventExists extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryEventExists(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final String... strings) {
            return asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.handleEventVerification(result);
        }
    }

    private static class InsertEvent extends AsyncTask<Event, Void, Void> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        InsertEvent(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(final Event... events) {
            asyncDao.insertEvent(events[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void nothing) {
            delegate.findEventId();
        }
    }

    private static class QueryUserId extends AsyncTask<Void, Void, List<Integer>> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryUserId(HabifyDao dao, HabitSetupRepository repo) {
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

    private static class QueryEventId extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryEventId(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final String... strings) {
            return asyncDao.getEventId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.setEventId(result);
        }
    }

    private static class QueryLocationExists extends AsyncTask<Double, Void, Integer> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryLocationExists(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final Double... doubles) {
            return asyncDao.getLocationId(doubles[0], doubles[1]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.handleLocationVerification(result);
        }
    }

    private static class InsertLocation extends AsyncTask<Location, Void, Void> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        InsertLocation(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(final Location... locations) {
            asyncDao.insertLocation(locations[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void nothing) {
            delegate.findLocationId();
        }
    }

    private static class QueryLocationId extends AsyncTask<Double, Void, Integer> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryLocationId(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(final Double... doubles) {
            return asyncDao.getLocationId(doubles[0], doubles[1]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.setLocationId(result);
        }
    }

    private static class InsertHabit extends AsyncTask<Habit, Void, Void> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        InsertHabit(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(final Habit... habits) {
            asyncDao.insertHabit(habits[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void nothing) {
            delegate.getHabitId();
        }
    }

    private static class QueryHabitId extends AsyncTask<String, Void, Integer> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryHabitId(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Integer doInBackground(String... strings) {
            return asyncDao.getHabitId(strings[0]);
        }
        @Override
        protected void onPostExecute(Integer result) {
            delegate.getDaysTable(result);
        }
    }

    private static class QueryAllDays extends AsyncTask<Void, Void, List<Day>> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        QueryAllDays(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected List<Day> doInBackground(Void... voids) {
            return asyncDao.getAllDays();
        }
        @Override
        protected void onPostExecute(List<Day> result) {
            delegate.generateScheduleEntries(result);
        }
    }

    private static class InsertSchedule extends AsyncTask<Schedule, Void, Void> {
        private HabifyDao asyncDao;
        private HabitSetupRepository delegate;
        InsertSchedule(HabifyDao dao, HabitSetupRepository repo) {
            asyncDao = dao;
            delegate = repo;
        }
        @Override
        protected Void doInBackground(Schedule... schedules) {
            for (int i = 0; i < schedules.length; i++) {
                asyncDao.insertSchedule(schedules[i]);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void nothing) {
            delegate.insertHabitCascadeFinishedLogger();
        }
    }
}
