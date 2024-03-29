package com.fuzzy.subsystems.graphql.out;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystems.graphql.input.GMonth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@GraphQLTypeOutObject("out_datetime")
public class GOutDateTime implements RemoteObject, Comparable<GOutDateTime> {

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Дата")
    private final GOutDate date;

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Время")
    private final GOutTime time;


    public GOutDateTime(int year, GMonth month, int day, int hour, int minute, int second) {
        date = new GOutDate(year, month, day);
        time = new GOutTime(hour, minute, second);
    }

    @Override
    public int compareTo(GOutDateTime o) {
        int res = date.compareTo(o.date);
        if (res == 0) {
            res = time.compareTo(o.time);
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GOutDateTime that = (GOutDateTime) o;

        return date.equals(that.date) && time.equals(that.time);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + time.hashCode();
        return result;
    }

    public static GOutDateTime of(LocalDateTime localDateTime) {
        return new GOutDateTime(
                localDateTime.getYear(), GMonth.of(localDateTime.getMonth()), localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond()
        );
    }

    public static GOutDateTime of(Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return null;
        }
        return of(LocalDateTime.ofInstant(instant, zoneId));
    }

    public static GOutDateTime of(long epochSecond, ZoneId zoneId) {
        return of(Instant.ofEpochSecond(epochSecond), zoneId);
    }
}
