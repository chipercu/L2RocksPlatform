package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@GraphQLTypeOutObject("zoned_date_time")
public class GZonedDateTime implements RemoteObject {

    private static final String PATTERN = "pattern";

    private ZonedDateTime zonedDateTime;

    public GZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Время в unix time формате")
    public long getUnixtimeMs() throws PlatformException {
        try {
            return zonedDateTime.toInstant().toEpochMilli();
        } catch (ArithmeticException e) {
            throw GeneralExceptionBuilder.buildArithmeticException(e);
        }
    }

    @GraphQLField
    @GraphQLDescription("Форматированное время")
    @GraphQLAuthControl({ AuthorizedContext.class })
    public String getFormatted(
            @GraphQLName(PATTERN)
            @GraphQLDescription("Шаблон форматирования: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html")
            final String pattern
    ) throws PlatformException {
        String innerPattern = StringUtils.isEmpty(pattern) ? "yyyy.MM.dd HH:mm:ss O" : pattern;
        DateTimeFormatter dateTimeFormatter;
        try {
            dateTimeFormatter = DateTimeFormatter.ofPattern(innerPattern, Locale.ENGLISH);
        } catch (IllegalArgumentException e) {
            throw GeneralExceptionBuilder.buildInvalidValueException(PATTERN, innerPattern);
        }
        return zonedDateTime.format(dateTimeFormatter);
    }

    public static GZonedDateTime of(Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return null;
        }
        return new GZonedDateTime(ZonedDateTime.ofInstant(instant, zoneId));
    }
}
