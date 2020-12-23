package com.neu.habify.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * A class to represent an award, which serves as a join table between habits, badges, and events.
 * This table keeps track of which badges were earned by which habit, and on what day. The display
 * boolean indicates whether or not this badge should be displayed on the shareable profile.
 */
@Entity(tableName = "awards",
        primaryKeys = {"user_id", "badge_id", "earned_on"},
        foreignKeys = {@ForeignKey(entity = User.class,
                                   parentColumns = "id",
                                   childColumns = "user_id"),
                       @ForeignKey(entity = Badge.class,
                                   parentColumns = "id",
                                   childColumns = "badge_id"),
                       @ForeignKey(entity = Event.class,
                                   parentColumns = "id",
                                   childColumns = "earned_on")})
public class Award {

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "badge_id")
    private int badgeId;

    @ColumnInfo(name = "earned_on")
    private int earnedOn;

    @ColumnInfo(name = "display")
    private boolean display;

    public Award(int userId, int badgeId, int earnedOn, boolean display) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.earnedOn = earnedOn;
        this.display = display;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public int getEarnedOn() {
        return earnedOn;
    }

    public void setEarnedOn(int earnedOn) {
        this.earnedOn = earnedOn;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
