package com.tobiasfried.rasa.utils

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit


object TimeUtility {

    const val MAX_DATE = 1893455999000L

    fun daysBetween(startTimestamp: Long, endTimestamp: Long): Int {
        return ChronoUnit.DAYS.between(Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()).toInt()
    }

    fun hoursBetween(startTimestamp: Long, endTimestamp: Long): Int {
        return ChronoUnit.HOURS.between(Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()).toInt()
    }

    fun formatDateShort(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("LLL d")
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()))
    }

}
