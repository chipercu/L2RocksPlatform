package com.fuzzy.subsystem.core.config;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;

@GraphQLTypeOutObject("first_day_of_week")
public enum FirstDayOfWeek implements RemoteObject, BaseEnum {

    MONDAY(1),

    SUNDAY(2);

    private final int id;

    FirstDayOfWeek(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static FirstDayOfWeek get(long id) {
        for (FirstDayOfWeek type : FirstDayOfWeek.values()) {
            if (type.intValue() == id) {
                return type;
            }
        }
        return null;
    }
}
