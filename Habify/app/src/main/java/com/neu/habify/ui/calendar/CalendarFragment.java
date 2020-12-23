package com.neu.habify.ui.calendar;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.neu.habify.R;

import com.neu.habify.data.entity.Habit;
import com.neu.habify.data.entity.Schedule;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i("CALENDAR_VIEW", "on view created");
        setUpCalendar();
    }

    private void setUpCalendar() {

        final CompactCalendarView calendarView =  Objects.requireNonNull(getView()).findViewById(R.id.calendar);
        final TextView month_year =  getView().findViewById(R.id.month_id);

        month_year.setText(LocalDateTime.now().getMonth() + " " + LocalDateTime.now().getYear());

        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
               List<Event> events = calendarView.getEvents(date);
               Context c = getActivity().getApplicationContext();
               if(events.size() > 0) {
                   Toast.makeText(c, "You have habits on selected date", Toast.LENGTH_SHORT).show();
               }
               else {
                   Toast.makeText(c, "No events", Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onMonthScroll(Date date) {
                Instant instant = date.toInstant();
                ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
                LocalDate ld = zdt.toLocalDate();
                month_year.setText(ld.getMonth().toString() + " " + ld.getYear());
            }
        });

        this.calendarViewModel.getSchedule().observe(getViewLifecycleOwner(), new Observer<List<Schedule>>() {
            @Override
            public void onChanged(List<Schedule> habits) {

                //add days of the week that have a habit
                Set<Integer> habit_days = new LinkedHashSet<>();
                for (Schedule habit : habits) {
                    habit_days.add(getDay(habit.getDayId()));
                }

                LocalDateTime start_date = LocalDateTime.now();

                Date to_convert = new GregorianCalendar(start_date.getYear(), 12, 31).getTime();
                LocalDateTime endDate = to_convert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                //add events to days of the week
                for (LocalDateTime date = start_date; date.isBefore(endDate); date = date.plusDays(1)) {
                    if (habit_days.contains(date.getDayOfWeek().getValue())) {
                        ZonedDateTime zoneId = date.atZone(ZoneId.systemDefault());
                        long localDateInMilli = zoneId.toInstant().toEpochMilli();

                        calendarView.addEvent(new Event(Color.BLACK, localDateInMilli));
                    }
                }
            }
        });
    }

    private int getDay(int day_id) {
        int day;
        switch (day_id) {
            case 1:
                day = Calendar.MONDAY;
                break;
            case 2:
                day = Calendar.TUESDAY;
                break;
            case 3:
                day = Calendar.WEDNESDAY;
                break;
            case 4:
                day = Calendar.THURSDAY;
                break;
            case 5:
                day = Calendar.FRIDAY;
                break;
            case 6:
                day = Calendar.SATURDAY;
                break;
            case 7:
                day = Calendar.SUNDAY;
                break;
            default:
                throw new IllegalArgumentException("Invalid id of the weekday");
        }
        return day;
    }

}
