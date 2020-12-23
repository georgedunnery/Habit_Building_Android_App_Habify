package com.neu.habify.data.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entity to represent a habit.
 */
@Entity(tableName = "habits",
        foreignKeys = {@ForeignKey(entity = User.class,
                                   parentColumns = "id",
                                   childColumns = "user_id"),
                       @ForeignKey(entity = Location.class,
                                   parentColumns = "id",
                                   childColumns = "location"),
                       @ForeignKey(entity = Event.class,
                                   parentColumns = "id",
                                   childColumns = "created")})
public class Habit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "location")
    private Integer location;

    @ColumnInfo(name= "gps")
    private boolean gps;

    @ColumnInfo(name = "created")
    private int created;

    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "end_time")
    private String endTime;

    @ColumnInfo(name = "bound")
    private Integer bound;

    @ColumnInfo(name = "active")
    private boolean active;

    public Habit(int userId, @NonNull String name, Integer location, boolean gps, int created,
                 String startTime, String endTime, Integer bound) {
        this.userId = userId;
        this.name = name;
        this.location = location;
        this.gps = gps;
        this.created = created;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bound = bound;
        this.active = true;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public boolean getGps() { return this.gps; }

    public void setGps(boolean gps) { this.gps = gps; }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
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

    public Integer getBound() {
        return bound;
    }

    public void setBound(Integer bound) {
        this.bound = bound;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
