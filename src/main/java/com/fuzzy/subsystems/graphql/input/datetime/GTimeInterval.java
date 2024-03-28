package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.main.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("time_interval")
public class GTimeInterval implements RemoteObject {

    private final GInputLocalTime beginLocalTime;
    private final GInputLocalTime endLocalTime;
    private final Integer offset;

    public GTimeInterval(
            @GraphQLDescription(Tool.BEGIN_LOCAL_TIME_DESCRIPTION)
            @NonNull @GraphQLName(Tool.BEGIN_LOCAL_TIME) GInputLocalTime beginLocalTime,
            @GraphQLDescription(Tool.END_LOCAL_TIME_DESCRIPTION)
            @NonNull @GraphQLName(Tool.END_LOCAL_TIME) GInputLocalTime endLocalTime,
            @GraphQLDescription(Tool.OFFSET_DESCRIPTION)
            @NonNull @GraphQLName(Tool.OFFSET) Integer offset) throws PlatformException {
        Tool.validateTimeInterval(beginLocalTime, endLocalTime, offset);
        this.beginLocalTime = beginLocalTime;
        this.endLocalTime = endLocalTime;
        this.offset = offset;
    }

    public @NonNull GInputLocalTime getBeginLocalTime() {
        return beginLocalTime;
    }

    public @NonNull GInputLocalTime getEndLocalTime() {
        return endLocalTime;
    }

    public @NonNull Integer getOffset() {
        return offset;
    }
}
