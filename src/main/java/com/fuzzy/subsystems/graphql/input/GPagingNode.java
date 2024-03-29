package com.fuzzy.subsystems.graphql.input;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Objects;

@GraphQLTypeInput("paging_node")
public class GPagingNode {

    private final ArrayList<GPagingElement> elements;
    private final int defaultLimit;
    private final long id;
    private final int limit;

    public GPagingNode(
            @GraphQLDescription("Параметры пейджинга для отдельных вложенных групп")
            @Nullable @GraphQLName("elements") ArrayList<GPagingElement> elements,
            @GraphQLDescription("Количество отображаемых элементов по-умолчанию")
            @NonNull @GraphQLName("default_limit") Integer defaultLimit,
            @GraphQLDescription("Идентификатор группы")
            @NonNull @GraphQLName("id") Long id,
            @NonNull @GraphQLName("limit") Integer limit) {
        this.elements = elements;
        this.defaultLimit = defaultLimit;
        this.id = id;
        this.limit = limit;
    }

    public long getId() {
        return id;
    }

    public int getLimit() {
        return Math.max(limit, 0);
    }

    public @Nullable ArrayList<GPagingElement> getElements() {
        return elements;
    }

    public int getDefaultLimit() {
        return Math.max(defaultLimit, 0);
    }

    public int getLimit(@Nullable Long elementId) {
        int limit = getDefaultLimit();
        if (elements != null) {
            for (GPagingElement element : elements) {
                if (Objects.equals(elementId, element.getId())) {
                    limit = element.getLimit();
                    break;
                }
            }
        }
        return limit;
    }
}