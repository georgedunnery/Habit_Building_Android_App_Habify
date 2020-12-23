package com.neu.habify.data.entity;

/**
 * A plain java object to hold the results of a complex sql query. The purpose of this is to make
 * more data available to display in the dashboard habits list.
 *
 * The columns in the sql query must match up by index with the columns here?
 *
 * https://developer.android.com/training/data-storage/room/accessing-data.html#query-multiple-tables
 */
public class DetailedHabitData {

    private int habitId;
    private String habitName;
    private Integer bound;
    private String startTime;
    private String endTime;
    private Integer created;
    private boolean gps;
    private Integer locationId;
    private String locationName;
    private String locationAddress;
    private int successes;
    private int failures;
    private int completed;
    private int succeededToday;

    /**
     *
     * @param habitId
     * @param habitName
     * @param bound
     * @param startTime
     * @param endTime
     * @param locationName
     * @param locationAddress
     * @param successes
     * @param failures
     * @param completed should only ever be 1 = completed today, or 0 = remaining task because (habit_id, event_id) must be unique in records table
     * @param succeededToday
     */
    public DetailedHabitData(int habitId, String habitName, Integer bound, String startTime, String endTime, String locationName, String locationAddress, int successes, int failures, int completed, int succeededToday) {
        this.habitId = habitId;
        this.habitName = habitName;
        this.bound = bound;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.successes = successes;
        this.failures = failures;
        this.completed = completed;
        this.succeededToday = succeededToday;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public Integer getBound() {
        return bound;
    }

    public void setBound(Integer bound) {
        this.bound = bound;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public boolean getGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public int getSuccesses() {
        return successes;
    }

    public void setSuccesses(int successes) {
        this.successes = successes;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getSucceededToday() {
        return succeededToday;
    }

    public void setSucceededToday(int succeededToday) {
        this.succeededToday = succeededToday;
    }
}
