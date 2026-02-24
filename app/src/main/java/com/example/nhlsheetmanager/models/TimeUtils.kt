package com.example.nhlsheetmanager.models

import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object TimeUtils {
    fun getInitialDelayForHourUtc(hour: Int = 10): Long {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        var nextRun = now.withHour(hour).truncatedTo(ChronoUnit.HOURS)

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }

        return Duration.between(now, nextRun).toMillis()
    }
}