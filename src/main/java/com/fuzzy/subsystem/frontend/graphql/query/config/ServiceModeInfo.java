package com.fuzzy.subsystem.frontend.graphql.query.config;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;

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
