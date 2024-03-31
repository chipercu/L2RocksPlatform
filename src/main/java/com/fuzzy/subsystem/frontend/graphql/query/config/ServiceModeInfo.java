package com.fuzzy.subsystem.frontend.graphql.query.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;

@GraphQLTypeOutObject("service_mode_info")
public class ServiceModeInfo implements RemoteObject {

    private final boolean enabled;
    private final String message;

    public ServiceModeInfo(boolean enabled, String message) {
        this.enabled = enabled;
        this.message = message;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Сервисный режим включен?")
    public boolean isEnabled() {
        return enabled;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Сообщение")
    public String getMessage() {
        return message;
    }
}
