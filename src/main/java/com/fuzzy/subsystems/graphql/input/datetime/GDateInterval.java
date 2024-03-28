package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.main.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("date_interval")
public class GDateInterval implements RemoteObject {

    private final GInputLocalDate beginLocalDate;
    private final GInputLocalDate endLocalDate;
    private final Integer offset;

    public GDateInterval(
            @GraphQLDescription(Tool.BEGIN_LOCAL_DATE_DESCRIPTION)
            @NonNull @GraphQLName(Tool.BEGIN_LOCAL_DATE) GInputLocalDate begin,
            @GraphQLDescription(Tool.END_LOCAL_DATE_DESCRIPTION)
            @NonNull @GraphQLName(Tool.END_LOCAL_DATE) GInputLocalDate end,
            @GraphQLDescription(Tool.OFFSET_DESCRIPTION)
            @NonNull @GraphQLName(Tool.OFFSET) Integer offset) throws PlatformException {
        Tool.validateDateInterval(begin, end, offset);
        this.beginLocalDate = begin;
        this.endLocalDate = end;
        this.offset = offset;
    }

    public @NonNull GInputLocalDate getBeginLocalDate() {
        return beginLocalDate;
    }

    public @NonNull GInputLocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public @NonNull Integer getOffset() {
        return offset;
    }

    @Override
    public @NonNull String toString() {
        return beginLocalDate.toString() + " - " + endLocalDate.toString();
    }
}