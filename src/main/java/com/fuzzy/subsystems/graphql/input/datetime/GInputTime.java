package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.platform.exception.PlatformException;
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
