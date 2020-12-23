package com.neu.habify.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.neu.habify.data.entity.Award;
import com.neu.habify.data.entity.Badge;
import com.neu.habify.data.entity.Day;
import com.neu.habify.data.entity.DetailedHabitData;
import com.neu.habify.data.entity.Event;
import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Location;
import com.neu.habify.data.entity.Record;
import com.neu.habify.data.entity.Schedule;
import com.neu.habify.data.entity.User;

import java.util.List;

/**
 * Interface which defines queries for the database.
 */
@Dao
public interface HabifyDao {

    @Query("SELECT count(*) FROM days")
    int numberEntriesInDays();

    @Query("SELECT count(*) FROM badges")
    int numberEntriesInBadges();

    @Query("SELECT count(*) FROM users")
    int numberEntriesInUsers();

    @Query("SELECT id FROM events WHERE date=:givenDate")
    Integer getEventId(String givenDate);

    @Query("SELECT id FROM locations WHERE latitude=:givenLatitude AND longitude=:givenLongitude")
    Integer getLocationId(Double givenLatitude, Double givenLongitude);

    @Query("SELECT * FROM locations WHERE id=:givenLocationId")
    Location getLocationById(Integer givenLocationId);

    @Query("SELECT id FROM users")
    List<Integer> getUserId();

    @Query("SELECT id, name FROM days")
    List<Day> getAllDays();

    @Query("SELECT id FROM days WHERE name=:givenName")
    Integer getDayId(String givenName);

    @Query("SELECT id FROM habits WHERE name=:givenName")
    Integer getHabitId(String givenName);

    @Query("SELECT * FROM users")
    List<User> getAllNames();

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Insert
    void insertDay(Day day);

    @Insert
    void insertBadge(Badge badge);

    @Insert
    void insertUser(User user);

    @Insert
    void insertEvent(Event event);

    @Insert
    void insertLocation(Location location);

    @Insert
    void insertHabit(Habit habit);

    @Insert
    void insertSchedule(Schedule schedule);

    @Insert
    void insertRecord(Record record);

    @Query("SELECT habit_id FROM schedules JOIN days ON days.id = schedules.day_id WHERE day_id=:givenDayId")
    List<Integer> getTodaysHabits(int givenDayId);

    @Query("SELECT * FROM schedules")
    LiveData<List<Schedule>> getSchedules();

    @Query("SELECT * FROM habits WHERE id IN(:habitIds) AND active = 1")
    LiveData<List<Habit>> getHabitsFromIdsList(Integer[] habitIds);

    @Query("SELECT badges.id, badges.name, badges.value, badges.icon FROM awards JOIN badges ON awards.badge_id = badges.id WHERE awards.user_id = :givenUser")
    LiveData<List<Badge>> getAllEarnedBadges(Integer givenUser);

    @Query("SELECT * FROM habits WHERE user_id=:givenUser ORDER BY active DESC")
    LiveData<List<Habit>> getAllHabitsOrderByActive(Integer givenUser);

    @Query("DELETE FROM users")
    void clearUsers();

    @Query("DELETE FROM days")
    void clearDays();

    @Query("DELETE FROM badges")
    void clearBadges();

    @Query("SELECT * FROM habits")
    LiveData<List<Habit>> getAllHabits();

    @Query("SELECT * FROM habits WHERE active=1")
    LiveData<List<Habit>> getActiveHabits();

    @Query("SELECT COUNT(*) FROM records WHERE habit_id = :habitId AND event_id > " +
            "(SELECT IFNULL(" +
            "(SELECT event_id FROM records WHERE habit_id = :habitId AND evaluation = 0 ORDER BY event_id DESC LIMIT 1)" +
            ", -1))")
    int countStreak(int habitId);

    @Query("INSERT INTO awards (user_id, badge_id, earned_on, display) VALUES (:userId, :badgeId, :eventId, 0)")
    void insertAward(int userId, int badgeId, int eventId);

    @Query("SELECT COUNT(*) FROM awards WHERE badge_id = :badgeId AND earned_on = :eventId")
    int checkAwards(int badgeId, int eventId);

    @Query("SELECT * FROM records WHERE evaluation=1")
    LiveData<List<Record>> getAllCompletedTasks();

    @Query("SELECT * FROM records JOIN (SELECT records.event_id, sum(case when records.evaluation = 0 then 1 else 0 end) as disqaulified FROM records group by records.event_id) qualifying_records ON records.event_id = qualifying_records.event_id WHERE disqaulified=0")
    LiveData<List<Record>> getAllPerfectDays();

    @Query("SELECT * FROM records WHERE habit_id = :habitId ORDER BY event_id DESC, time DESC LIMIT 1")
    List<Record> getLastRecord(int habitId);

    @Query("SELECT * FROM awards JOIN events ON awards.earned_on = events.id " +
            "JOIN records ON events.id = records.event_id " +
            "WHERE habit_id = :habitId ORDER BY event_id DESC, time DESC LIMIT 1")
    List<Award> getLastAward(int habitId);

    @Query("SELECT * FROM badges WHERE id = :badgeId")
    List<Badge> getBadge(int badgeId);

    // I couldn't figure out how to get this onto multiple lines
    @Query("SELECT habits.id as habitId, habits.name as habitName, habits.bound as bound, habits.start_time as startTime, habits.end_time as endTime, habits.created as created, habits.gps as gps, locations.id as locationId, locations.name as locationName, locations.address as locationAddress, sum(case when records.evaluation=1 then 1 else 0 end) as successes, sum(case when records.evaluation=0 then 1 else 0 end) as failures, sum(case when records.event_id=:givenEventId then 1 else 0 end) as completed, sum(case when records.event_id=:givenEventId and records.evaluation=1 then 1 else 0 end) as succeededToday from habits join locations on habits.location = locations.id left join records on habits.id = records.habit_id where habitId in (:givenHabitIds) and habits.active=1 group by habits.id order by completed")
    LiveData<List<DetailedHabitData>> getDetailedDashboardHabits(Integer givenEventId, Integer[] givenHabitIds);
}
