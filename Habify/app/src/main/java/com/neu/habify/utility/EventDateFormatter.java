package com.neu.habify.utility;

import java.time.LocalTime;
import java.util.Calendar;

public abstract class EventDateFormatter {

    public static String getCurrentDate() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        // The month is returned as 0-11 instead of 1-12 according to java documentation
        // Leaving it as is to avoid confusion if we ever need to the other way, string -> Date
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return "" + month + "/" + day + "/" + year;
    }

    public static String getDayOfWeekAsString() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                throw new IllegalArgumentException("EventDateFormatter encountered unknown week day.");
        }
    }

    public static String getTimeString() {
        // Gets time as the 24 hour time
        return LocalTime.now().toString();
    }
}
