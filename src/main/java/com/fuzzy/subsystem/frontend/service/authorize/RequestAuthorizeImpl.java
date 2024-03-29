package com.fuzzy.subsystem.frontend.service.authorize;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.component.frontend.engine.authorize.RequestAuthorize;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryPool;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.component.Component;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.component.authcontext.AuthContextComponent;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.BuilderAuthContext;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.employee.BuilderEmployeeSessionAuthContext;

public class RequestAuthorizeImpl implements RequestAuthorize {

    private AuthContextComponent authContextComponent;

    public RequestAuthorizeImpl(Component component, GRequest gRequest, ResourceProvider resources) throws PlatformException {
        this.authContextComponent = new AuthContextComponent(
                (FrontendSubsystem) component,
                gRequest,
                resources
        );
    }

    @Override
    public QueryPool.Priority getRequestPriority() {
        BuilderAuthContext builderAuthContext = authContextComponent.getBuilderAuthContext();
        if (builderAuthContext == null || builderAuthContext instanceof BuilderEmployeeSessionAuthContext) {
            return QueryPool.Priority.HIGH;
        } else {
            return QueryPool.Priority.LOW;
        }
    }

    @Override
    public UnauthorizedContext authorize(ContextTransactionRequest context) throws PlatformException {
        return authContextComponent.getAuthContext(context);
    }

    public static class Builder implements RequestAuthorize.Builder {

        @Override
        public RequestAuthorize build(Component component, GRequest gRequest, ResourceProvider resources) throws PlatformException {
            return new RequestAuthorizeImpl(component, gRequest, resources);
        }
    }

}
