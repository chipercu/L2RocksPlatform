package com.fuzzy.subsystems.graphql.enums;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.rdao.database.utils.BaseEnum;

@GraphQLTypeOutObject("activity_collection_type")
public enum EnableMonitoringType implements RemoteObject, BaseEnum {

    @GraphQLDescription("Выключено")
    DISABLED(0),
    @GraphQLDescription("Простой мониторинг")
    SIMPLE(1),
    @GraphQLDescription("Расширенный мониторинг")
    EXTENDED(2);

    private final int id;

    EnableMonitoringType(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static EnableMonitoringType get(long id) {
        for (EnableMonitoringType enableMonitoringType : EnableMonitoringType.values()) {
            if (enableMonitoringType.intValue() == id) {
                return enableMonitoringType;
            }
        }
        return null;
    }
}