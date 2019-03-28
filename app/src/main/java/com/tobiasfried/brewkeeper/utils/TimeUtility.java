package com.tobiasfried.brewkeeper.utils;

import android.os.Build;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class TimeUtility {

    public static int daysBetween(long startTimestamp, long endTimestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return (int) ChronoUnit.DAYS.between(Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                    Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            return Days.daysBetween(new org.joda.time.LocalDate(startTimestamp), new org.joda.time.LocalDate(endTimestamp)).getDays();
        }
    }

    public static int hoursBetween(long startTimestamp, long endTimestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return (int) ChronoUnit.HOURS.between(Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                    Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            return Hours.hoursBetween(new org.joda.time.Instant(startTimestamp), new org.joda.time.Instant(endTimestamp)).getHours();
        }
    }

    public static String formatDateShort(long timestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL d");
            return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
        } else {
            org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM d");
            return formatter.print(timestamp);
        }
    }

}
