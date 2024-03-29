package com.fuzzy.subsystem.core.config;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;

@GraphQLTypeOutObject("server_status")
public enum ServerStatus implements RemoteObject, BaseEnum {

    NOT_INIT(1),

    ACTIVE(2);

    private final int id;

    ServerStatus(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static ServerStatus get(long id) {
        for (ServerStatus item : ServerStatus.values()) {
            if (item.intValue() == id) {
                return item;
            }
        }
        return null;
    }
}