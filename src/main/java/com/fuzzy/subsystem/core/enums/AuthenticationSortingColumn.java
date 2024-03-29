package com.fuzzy.subsystem.core.enums;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;

@GraphQLTypeOutObject("authentication_sorting_column")
public enum AuthenticationSortingColumn implements RemoteObject, BaseEnum {

    NAME(1),
    TYPE(2);

    private final int id;

    AuthenticationSortingColumn(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static AuthenticationSortingColumn get(int id) {
        for (AuthenticationSortingColumn sortedColumn : AuthenticationSortingColumn.values()) {
            if (sortedColumn.intValue() == id) {
                return sortedColumn;
            }
        }
        return null;
    }
}
