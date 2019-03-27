package com.tobiasfried.brewkeeper.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeUtility {

    public static int daysBetween(long timestamp1, long timestamp2) {
        return (int) ChronoUnit.DAYS.between(Instant.ofEpochMilli(timestamp1).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(timestamp2).atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static int hoursBetween(long timestamp1, long timestamp2) {
        return (int) ChronoUnit.HOURS.between(Instant.ofEpochMilli(timestamp1).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(timestamp2).atZone(ZoneId.systemDefault()).toLocalDate());
    }

}
