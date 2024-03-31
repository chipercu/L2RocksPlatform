package com.fuzzy.subsystem.core.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.database.utils.BaseEnum;

/**
 * Created by kris on 17.03.17.
 */
@GraphQLTypeOutObject("display_name_format")
public enum DisplayNameFormat implements RemoteObject, BaseEnum {

    FIRST_SECOND(1),

    SECOND_FIRST(2);

    private final int id;

    DisplayNameFormat(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static DisplayNameFormat get(long id) {
        for (DisplayNameFormat item : DisplayNameFormat.values()) {
            if (item.intValue() == id) {
                return item;
            }
        }
        return null;
    }
}
