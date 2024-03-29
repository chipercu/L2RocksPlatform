package com.fuzzy.subsystem.core.graphql.input;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@GraphQLTypeInput("access_role_paging_element")
public class GAccessRolePagingElement {

    private final Long accessRoleId;
    private final String type;
    private final int limit;

    public GAccessRolePagingElement(
            @GraphQLDescription("Идентификатор роли доступа")
            @Nullable @GraphQLName("access_role_id") Long accessRoleId,
            @GraphQLDescription("Тип")
            @Nullable @GraphQLName("type") String type,
            @GraphQLDescription("Количество отображаемых элементов")
            @NonNull @GraphQLName("limit") Integer limit) {
        this.accessRoleId = accessRoleId;
        this.type = type;
        this.limit = limit;
    }

    public @Nullable Long getAccessRoleId() {
        return accessRoleId;
    }

    public @Nullable String getType() {
        return type;
    }

    public int getLimit() {
        return Math.max(limit, 0);
    }
}