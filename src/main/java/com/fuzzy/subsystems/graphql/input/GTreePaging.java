package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

@GraphQLTypeInput("tree_paging")
public class GTreePaging {

    private final ArrayList<GPagingElement> elements;
    private final Integer rootLimit;
    private final int defaultLimit;

    public GTreePaging(
            @GraphQLDescription("Параметры пейджинга для отдельных групп")
            @Nullable @GraphQLName("elements") ArrayList<GPagingElement> elements,
            @GraphQLDescription("Количество отображаемых элементов в корне")
            @Nullable @GraphQLName("root_limit") Integer rootLimit,
            @GraphQLDescription("Количество отображаемых элементов по-умолчанию")
            @NonNull @GraphQLName("default_limit") Integer defaultLimit) {
        this.elements = elements;
        this.rootLimit = rootLimit;
        this.defaultLimit = defaultLimit;
    }

    public @Nullable ArrayList<GPagingElement> getElements() {
        return elements;
    }

    public int getDefaultLimit() {
        return Math.max(defaultLimit, 0);
    }

    public @NonNull Integer getRootLimit() {
        return rootLimit == null ? 0 : rootLimit;
    }

    public int getLimit(@Nullable Long nodeId) {
        int limit = getDefaultLimit();
        if (nodeId == null) {
            if (rootLimit != null) {
                limit = getRootLimit();
            }
        } else if (elements != null) {
            for (GPagingElement element : elements) {
                if (element != null && nodeId == element.getId()) {
                    limit = element.getLimit();
                    break;
                }
            }
        }
        return limit;
    }
}
