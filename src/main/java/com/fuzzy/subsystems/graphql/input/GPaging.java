package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("paging")
public class GPaging {

    private final int limit;

    public GPaging(
            @GraphQLDescription("Количество отображаемых элементов")
            @NonNull @GraphQLName("limit") Integer limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return Math.max(limit, 0);
    }
}
