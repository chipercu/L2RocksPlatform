package com.fuzzy.subsystem.core.graphql.input;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

@GraphQLTypeInput("access_role_paging")
public class GAccessRolePaging {

    private final ArrayList<GAccessRolePagingElement> elements;
    private final int defaultLimit;

    public GAccessRolePaging(
            @GraphQLDescription("Параметры пейджинга для отдельных ролей доступа")
            @Nullable @GraphQLName("elements") ArrayList<GAccessRolePagingElement> elements,
            @GraphQLDescription("Количество отображаемых элементов по-умолчанию")
            @NonNull @GraphQLName("default_limit") Integer defaultLimit) {
        this.elements = elements;
        this.defaultLimit = defaultLimit;
    }

    public @Nullable ArrayList<GAccessRolePagingElement> getElements() {
        return elements;
    }

    public int getDefaultLimit() {
        return Math.max(defaultLimit, 0);
    }

    public int getLimit(@Nullable Long accessRoleId, @Nullable String type) {
        int limit = getDefaultLimit();
        if (elements != null) {
            for (GAccessRolePagingElement element : elements) {
                if (accessRoleId == null) {
                    if (element.getAccessRoleId() == null) {
                        limit = element.getLimit();
                        break;
                    }
                } else if (accessRoleId.equals(element.getAccessRoleId()) &&
                        type != null && type.equals(element.getType())) {
                    limit = element.getLimit();
                    break;
                }
            }
        }
        return limit;
    }
}
