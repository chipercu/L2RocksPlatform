package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("paging_element")
public class GPagingElement {

    private final long id;
    private final int limit;

    public GPagingElement(
            @GraphQLDescription("Идентификатор группы")
            @NonNull @GraphQLName("id") Long id,
            @GraphQLDescription("Количество отображаемых элементов")
            @NonNull @GraphQLName("limit") Integer limit) {
        this.id = id;
        this.limit = limit;
    }

    public long getId() {
        return id;
    }

    public int getLimit() {
        return Math.max(limit, 0);
    }
}