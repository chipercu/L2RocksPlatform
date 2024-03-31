package com.fuzzy.subsystems.graphql.enums;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.database.utils.BaseEnum;

@GraphQLTypeOutObject("sort_direction")
public enum SortingDirection implements RemoteObject, BaseEnum {

    ASC(1),
    DESC(2);

    private final int id;

    SortingDirection(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public String toSQLString() {
        return switch (this) {
            case ASC -> "ASC";
            case DESC -> "DESC";
        };
    }

    public static SortingDirection get(long id) {
        for (SortingDirection sortingDirection : SortingDirection.values()) {
            if (sortingDirection.intValue() == id) {
                return sortingDirection;
            }
        }
        return null;
    }
}