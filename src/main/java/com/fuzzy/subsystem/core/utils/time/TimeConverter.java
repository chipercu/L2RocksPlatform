package com.fuzzy.subsystem.core.utils.time;

import com.fuzzy.subsystems.graphql.input.datetime.GInputLocalDate;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeConverter {

    public static InstantInterval convertLocalToUtc(GInputLocalDate beginLocalDate, GInputLocalDate endLocalDate, ZoneId zoneId) {
        return new InstantInterval(
                zonedDateTimeOf(beginLocalDate, zoneId).toInstant(),
                zonedDateTimeOf(endLocalDate, zoneId).plusDays(1).toInstant()
        );
    }

    public static InstantInterval convertLocalToUtc(GInputLocalDate date, ZoneId zoneId) {
        ZonedDateTime begin = zonedDateTimeOf(date, zoneId);
        return new InstantInterval(begin.toInstant(), begin.plusDays(1).toInstant());
    }

    public static Instant convertUtcToLocal(Instant utcTime, ZoneId zoneId) {
        return utcTime.atZone(zoneId).toInstant();
    }

    public static List<InstantInterval> splitToDays(InstantInterval utcInterval, ZoneId zoneId) {
        ZonedDateTime zonedBeginTime = utcInterval.getBegin().atZone(zoneId);
        ZonedDateTime zonedBeginDay = ZonedDateTime.of(zonedBeginTime.toLocalDate(), LocalTime.MIDNIGHT, zoneId);
        ZonedDateTime zonedEndTime = utcInterval.getEnd().atZone(zoneId);

        List<InstantInterval> days = new ArrayList<>();
        while (zonedBeginDay.isBefore(zonedEndTime)) {
            ZonedDateTime zonedNextDay = zonedBeginDay.plusDays(1);
            days.add(new InstantInterval(zonedBeginDay.toInstant(), zonedNextDay.toInstant()));
            zonedBeginDay = zonedNextDay;
        }
        return days;
    }

    private static ZonedDateTime zonedDateTimeOf(GInputLocalDate date, ZoneId timeZoneId) {
        return ZonedDateTime.of(
                date.getYear(),
                date.getMonth().intValue(),
                date.getDay(),
                0,
                0,
                0,
                0,
                timeZoneId
        );
    }
}
