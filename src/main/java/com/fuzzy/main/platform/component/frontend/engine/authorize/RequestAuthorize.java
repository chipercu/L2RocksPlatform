package com.fuzzy.main.platform.component.frontend.engine.authorize;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryPool;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.component.Component;

public interface RequestAuthorize {

    QueryPool.Priority getRequestPriority();

    UnauthorizedContext authorize(ContextTransactionRequest context) throws PlatformException;

    interface Builder {
        RequestAuthorize build(Component component, GRequest gRequest, ResourceProvider resources) throws PlatformException;
    }
}
