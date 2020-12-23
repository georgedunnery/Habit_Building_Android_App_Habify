package com.neu.habify.ui.dashboard;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neu.habify.data.entity.DetailedHabitData;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.room.DashboardRepository;
import com.neu.habify.utility.EventDateFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// Had to extend AndroidViewModel to have access to 'app': is required for repo implementations
public class DashboardViewModel extends AndroidViewModel {

    private static final String DASH_VIEW_TAG = "DashboardViewModel";
    private MutableLiveData<String> mText;
    private DashboardRepository dashRepo;
    private LiveData<List<DetailedHabitData>> habits;
    private DashboardFragment callback;

    private Habit confirmingHabit;
    private boolean evaluation = true;
    private Double latitude;
    private Double longitude;

    public DashboardViewModel(Application app) {
        super(app);
        mText = new MutableLiveData<>();
        mText.setValue("This is the dashboard fragment");
        dashRepo = new DashboardRepository(app, this);
        setupHabitsList();
    }

    public void receiveCallback(DashboardFragment callback) {
        this.callback = callback;
        setupHabitsList();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<DetailedHabitData>> getHabits() {
        Log.i(DASH_VIEW_TAG, "getHabits");
        return this.habits;
    }

    private void setupHabitsList() {
        Log.i(DASH_VIEW_TAG, "getHabitsList");
        dashRepo.getTodaysHabits(EventDateFormatter.getDayOfWeekAsString());
    }

    public void setHabits(LiveData<List<DetailedHabitData>> habits) {
        this.habits = habits;
        this.callback.notifyHabitsListIsReady();
        Log.i(DASH_VIEW_TAG, "notify that the habits list is ready");
    }

    /**
     * This method must always be called before habit record insertion.
     * @param habit
     */
    public void setConfirmingHabit(Habit habit) {
        this.confirmingHabit = habit;
    }

    /**
     * This starts a cascade for recording habit location with the gps verification feature. Be sure
     * to set the confirming habit first.
     *
     * Set the lat/long of user current location, acquired in the dashboard fragment
     * Query the coordinates of the location from the database
     * Compare the results, set the evalution to true or false
     * Insert the record
     * Announce the completion in a toast using the callback
     *
     * (If/when the dash list is separated by completed/remaining, list should auto update)
     *
     * @param location
     */
    public void recordHabitWithGpsVerification(android.location.Location location) {
        if (this.confirmingHabit == null) {
            Log.i(DASH_VIEW_TAG, "WARNING: confirming habit was null, no action will be taken!");
        }
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        dashRepo.getExpectedCoordinates(this.confirmingHabit.getLocation());
    }

    public void acceptExpectCoordinates(Double expectedLatitude, Double expectedLongitude) {
        boolean evaluation;
        // According to this resource, the 3rd decimal place is a granularity of 110m
        // https://gizmodo.com/how-precise-is-one-degree-of-longitude-or-latitude-1631241162
        Double userLat = coordinateRounder(this.latitude);
        Double userLon = coordinateRounder(this.longitude);
        Double expLat = coordinateRounder(expectedLatitude);
        Double expLon = coordinateRounder(expectedLongitude);
        // Match to the expected coordinates, with some leniency +/- 0.001 on both dimensions
        // Basically a 0.003 x 0.003 box of acceptable area overlap
        if ((userLat.equals(expLat - 0.01) || userLat.equals(expLat) || userLat.equals(expLat + 0.01))
                && (userLon.equals(expLon - 0.01) || userLon.equals(expLon) || userLon.equals(expLon - 0.01))) {
            evaluation = true;
        }
        else {
            evaluation = false;
        }
        Log.i(DASH_VIEW_TAG, "User lat/lng = [" + userLat + "," + userLon + "] vs. Expected lat/lng = [" + expLat + "," + expLon + "]  ->  evaluated as success = " + evaluation);
        // Insert the record with the decided evaluation based on GPS coordinate comparison from above
        recordHabitCompletion(evaluation);
    }

    /**
     * This is a continuation of the habit record insertion cascade from the gps verification routine,
     * or the starting place for habits that do not use gps verification. Be sure to set the
     * confirming habit first.
     * @param evaluation
     */
    public void recordHabitCompletion(boolean evaluation) {
        this.evaluation = evaluation;
        dashRepo.insertRecord(this.confirmingHabit.getId(), evaluation);
    }

    public void notifyHabitHasBeenRecorded() {

        /**
         * compute and may add award for this record
         * Steps:
         *   1. get the newly insert record object
         *   2. call BadgeUtils.addNewAward(habitId)
         *   3. if evaluation is true, call BadgeUtils.displayBadgeCongratsDialog(activity, badge)
         *                             to display badge congrats dialog
         *                             (get badge object via badge_id from inserted award)
         * These are all done via nested async calls in BadgeUtils.addNewAward
         */
        dashRepo.getLastInsertedRecord(this.confirmingHabit.getId());

        String msg = "The habit " + this.confirmingHabit.getName() + " was recorded as";
        if (this.evaluation) {
            msg += " successful for today. Great work!";
        }
        else {
            msg += " incomplete for today. Keep trying!";
        }
        this.callback.reportHabitCompletionStatus(msg);
    }

    private double coordinateRounder(double value) {
        return new BigDecimal(Double.toString(value))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

    }

    // TODO not sure if this is nullsafe
    public DashboardFragment getFragmentCallback() {
        return this.callback;
    }
}