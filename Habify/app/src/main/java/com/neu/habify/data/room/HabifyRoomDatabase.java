package com.neu.habify.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.neu.habify.data.entity.Award;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Day;
import com.neu.habify.data.entity.Event;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Location;
import com.neu.habify.data.entity.Record;
import com.neu.habify.data.entity.Schedule;
import com.neu.habify.data.entity.User;

/**
 * Abstract class responsible for serving a singleton instance of the database.
 */
@Database(entities = {Award.class,
                      Badge.class,
                      Day.class,
                      Event.class,
                      Habit.class,
                      Location.class,
                      Record.class,
                      Schedule.class,
                      User.class},
          version = 1)
public abstract class HabifyRoomDatabase extends RoomDatabase {

    public abstract HabifyDao habifyDao();
    private static HabifyRoomDatabase INSTANCE;

    static HabifyRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (HabifyRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        HabifyRoomDatabase.class, "habify_database").build();
            }
        }
        return INSTANCE;
    }
}
