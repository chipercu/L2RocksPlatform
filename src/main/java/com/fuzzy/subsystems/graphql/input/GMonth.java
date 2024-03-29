package com.fuzzy.subsystems.graphql.input;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Month;

@GraphQLTypeOutObject("month")
public enum GMonth implements RemoteObject, BaseEnum {

    JANUARY(Month.JANUARY),
    FEBRUARY(Month.FEBRUARY),
    MARCH(Month.MARCH),
    APRIL(Month.APRIL),
    MAY(Month.MAY),
    JUNE(Month.JUNE),
    JULY(Month.JULY),
    AUGUST(Month.AUGUST),
    SEPTEMBER(Month.SEPTEMBER),
    OCTOBER(Month.OCTOBER),
    NOVEMBER(Month.NOVEMBER),
    DECEMBER(Month.DECEMBER);

    private final Month month;

    GMonth(@NonNull Month month) {
        this.month = month;
    }

    @Override
    public int intValue() {
        return month.getValue();
    }

    public @NonNull Month getValue() {
        return month;
    }

    public static @Nullable GMonth of(@NonNull Month month) {
        for(GMonth value : values()) {
            if (value.intValue() == month.getValue()) {
                return value;
            }
        }
        return null;
    }
}
