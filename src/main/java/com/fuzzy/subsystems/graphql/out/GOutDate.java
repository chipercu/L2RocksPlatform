package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystems.graphql.input.GMonth;

import java.time.LocalDate;

@GraphQLTypeOutObject("out_date")
public class GOutDate implements RemoteObject, Comparable<GOutDate> {

    private final int year;
    private final GMonth month;
    private final int day;

    public GOutDate(int year, GMonth month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Год")
    public int getYear() {
        return year;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Месяц")
    public GMonth getMonth() {
        return month;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("День")
    public int getDay() {
        return day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GOutDate gOutDate = (GOutDate) o;

        if (year != gOutDate.year) return false;
        if (day != gOutDate.day) return false;
        return month == gOutDate.month;
    }

    @Override
    public int compareTo(GOutDate o) {
        int result = Integer.compare(year, o.year);
        if (result == 0) {
            result = Integer.compare(month.intValue(), o.month.intValue());
            if (result == 0) {
                result = Integer.compare(day, o.day);
            }
        }
        return result;
    }

    public static GOutDate of(LocalDate localDate) {
        return new GOutDate(localDate.getYear(), GMonth.of(localDate.getMonth()), localDate.getDayOfMonth());
    }
}
