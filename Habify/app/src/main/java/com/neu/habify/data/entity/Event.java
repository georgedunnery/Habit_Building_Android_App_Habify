package com.neu.habify.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity to represent an event. This is just a date to avoid potentially storing the same date
 * many times and wasting user storage (ex. the user completes 50 daily habits, using a join to
 * store the date once is more efficient than storing the date with each record). We still record
 * the specific time along with the appropriate record.
 */
@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "date")
    private String date;

    public Event(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
