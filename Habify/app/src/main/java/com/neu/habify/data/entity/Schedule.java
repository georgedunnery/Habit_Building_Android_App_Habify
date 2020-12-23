package com.neu.habify.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Entity to represent the schedule for a habit. Serves as a join table between a habits and days.
 */
@Entity(tableName = "schedules",
        primaryKeys = {"habit_id", "day_id"},
        foreignKeys = {@ForeignKey(entity = Habit.class,
                                   parentColumns = "id",
                                   childColumns = "habit_id"),
                       @ForeignKey(entity = Day.class,
                                   parentColumns = "id",
                                   childColumns = "day_id")})
public class Schedule {

    @ColumnInfo(name = "habit_id")
    private int habitId;

    @ColumnInfo(name = "day_id")
    private int dayId;

    public Schedule(int habitId, int dayId) {
        this.habitId = habitId;
        this.dayId = dayId;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }
}
