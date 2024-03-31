package com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct;

import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.querypool.QueryPool;

public record GExecutionStatistics(
        UnauthorizedContext authContext,
        QueryPool.Priority priority,
        long timeWait,
        long timeAuth,
        long timeExec,
        String accessDenied
) {

}