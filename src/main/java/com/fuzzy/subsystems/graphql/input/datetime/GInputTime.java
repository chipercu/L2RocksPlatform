package com.fuzzy.subsystems.graphql.input.datetime;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import com.infomaximum.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("input_time")
public class GInputTime implements RemoteObject {

    private static final String LOCAL_TIME = "local_time";

    private final GInputLocalTime localTime;
    private final Integer offset;

    public GInputTime(
            @GraphQLDescription("Локальное время")
            @NonNull @GraphQLName(LOCAL_TIME) GInputLocalTime localTime,
            @GraphQLDescription(Tool.OFFSET_DESCRIPTION)
            @NonNull @GraphQLName(Tool.OFFSET) Integer offset) throws PlatformException {
        Tool.validateOffset(offset);
        this.localTime = localTime;
        this.offset = offset;
    }

    public @NonNull GInputLocalTime getLocalTime() {
        return localTime;
    }

    public @NonNull Integer getOffset() {
        return offset;
    }
}
