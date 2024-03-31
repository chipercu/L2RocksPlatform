package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.graphql.input.GFilterOperation;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("period_filter")
public class GPeriodFilter implements RemoteObject {

    private static final String OPERATION = "operation";

    private final GFilterOperation operation;
    private final GInputLocalDate beginLocalDate;
    private final GInputLocalDate endLocalDate;
    private final Integer offset;

    public GPeriodFilter(
            @GraphQLDescription("Действие")
            @NonNull @GraphQLName(OPERATION) GFilterOperation operation,
            @GraphQLDescription(Tool.BEGIN_LOCAL_DATE_DESCRIPTION)
            @NonNull @GraphQLName(Tool.BEGIN_LOCAL_DATE) GInputLocalDate beginLocalDate,
            @GraphQLDescription(Tool.END_LOCAL_DATE_DESCRIPTION)
            @NonNull @GraphQLName(Tool.END_LOCAL_DATE) GInputLocalDate endLocalDate,
            @GraphQLDescription(Tool.OFFSET_DESCRIPTION)
            @NonNull @GraphQLName(Tool.OFFSET) Integer offset) throws PlatformException {
        Tool.validateDateInterval(beginLocalDate, endLocalDate, offset);
        this.operation = operation;
        this.beginLocalDate = beginLocalDate;
        this.endLocalDate = endLocalDate;
        this.offset = offset;
    }

    public @NonNull GFilterOperation getOperation() {
        return operation;
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
