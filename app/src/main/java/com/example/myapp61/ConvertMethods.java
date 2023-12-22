package com.example.myapp61;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertMethods {
    public static String ZoneDateTimeToString(ZonedDateTime reminderDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return reminderDateTime.format(formatter);
    }

    public static long ZoneDateTimeToToMillis(ZonedDateTime dateTime) {
        return dateTime.toInstant().toEpochMilli();
    }
}
