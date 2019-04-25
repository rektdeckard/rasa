package com.tobiasfried.rasa.utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;


public class TimeUtility {

    public static final long MAX_DATE = 1893455999000L;

    public static int daysBetween(long startTimestamp, long endTimestamp) {
        return (int) ChronoUnit.DAYS.between(Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static int hoursBetween(long startTimestamp, long endTimestamp) {
        return (int) ChronoUnit.HOURS.between(Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static String formatDateShort(long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL d");
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
    }

}
