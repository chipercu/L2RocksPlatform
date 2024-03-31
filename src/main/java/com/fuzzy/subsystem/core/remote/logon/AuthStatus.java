package com.fuzzy.subsystem.core.remote.logon;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("auth_status")
public enum AuthStatus implements RemoteObject {

    SUCCESS(0),

    INVALID_LOGON(1),

    DISABLED_LOGON(2),

    EXPIRED_PASSWORD(3),

    INVALID_LOGON_AND_MAX_LOGON_ATTEMPTS_EXCEED(4),

    NO_PRIVILEGES(5);

    private final int id;

    AuthStatus(int id) {
        this.id = id;
    }

    public static AuthStatus get(long id) {
        for (AuthStatus item : AuthStatus.values()) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }
}
