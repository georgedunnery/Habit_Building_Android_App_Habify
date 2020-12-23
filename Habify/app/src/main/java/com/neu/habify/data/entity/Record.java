package com.neu.habify.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Entity to represent a record, when the user either succeeded or failed to complete a habit task.
 * Serves as a join table between habits and events.
 */
@Entity(tableName = "records",
        primaryKeys = {"habit_id", "event_id"},
        foreignKeys = {@ForeignKey(entity = Habit.class,
                                   parentColumns = "id",
                                   childColumns = "habit_id"),
                       @ForeignKey(entity = Event.class,
                                   parentColumns = "id",
                                   childColumns = "event_id")})
public class Record {

    @ColumnInfo(name = "habit_id")
    private int habitId;

    @ColumnInfo(name = "event_id")
    private int eventId;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "evaluation")
    private boolean evaluation;

    public Record(int habitId, int eventId, @NonNull String time, boolean evaluation) {
        this.habitId = habitId;
        this.eventId = eventId;
        this.time = time;
        this.evaluation = evaluation;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEvaluation() {
        return evaluation;
    }

    public void setEvaluation(boolean evaluation) {
        this.evaluation = evaluation;
    }
}
