package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@GraphQLTypeOutObject("out_time")
public class GOutTime implements RemoteObject, Comparable<GOutTime> {

    private final int hour;
    private final int minute;
    private final int second;

    public GOutTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Час")
    public int getHour() {
        return hour;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Минута")
    public int getMinute() {
        return minute;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Секунда")
    public int getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GOutTime gOutTime = (GOutTime) o;

        if (hour != gOutTime.hour) return false;
        if (minute != gOutTime.minute) return false;
        return second == gOutTime.second;
    }

    @Override
    public int compareTo(GOutTime o) {
        int res = Integer.compare(hour, o.hour);
        if (res == 0) {
            res = Integer.compare(minute, o.minute);
            if (res == 0) {
                res = Integer.compare(second, o.second);
            }
        }
        return res;
    }

    public static GOutTime of(Instant instant, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return new GOutTime(localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
    }

    public static GOutTime of(long epochSecond, ZoneId zoneId) {
        return of(Instant.ofEpochSecond(epochSecond), zoneId);
    }

    public static GOutTime of(LocalTime localTime) {
        return new GOutTime(localTime.getHour(), localTime.getMinute(), localTime.getSecond());
    }
}
