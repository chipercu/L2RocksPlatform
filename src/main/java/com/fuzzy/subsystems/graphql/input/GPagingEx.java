package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Objects;

@GraphQLTypeInput("paging_ex")
public class GPagingEx {

    private final ArrayList<GPagingElement> elements;
    private final int defaultLimit;

    public GPagingEx(
            @GraphQLDescription("Параметры пейджинга для отдельных групп")
            @Nullable @GraphQLName("elements") ArrayList<GPagingElement> elements,
            @GraphQLDescription("Количество отображаемых элементов по-умолчанию")
            @NonNull @GraphQLName("default_limit") Integer defaultLimit) {
        this.elements = elements;
        this.defaultLimit = defaultLimit;
    }

    public ArrayList<GPagingElement> getElements() {
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