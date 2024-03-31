package com.fuzzy.platform.component.frontend.engine.authorize;

import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.component.Component;

public interface RequestAuthorize {

    QueryPool.Priority getRequestPriority();

    UnauthorizedContext authorize(ContextTransactionRequest context) throws PlatformException;

    interface Builder {
        RequestAuthorize build(Component component, GRequest gRequest, ResourceProvider resources) throws PlatformException;
    }
}
