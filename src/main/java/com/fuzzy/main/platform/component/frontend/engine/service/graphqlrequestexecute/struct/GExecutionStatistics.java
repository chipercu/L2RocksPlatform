package com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct;

import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.querypool.QueryPool;

public record GExecutionStatistics(
        UnauthorizedContext authContext,
        QueryPool.Priority priority,
        long timeWait,
        long timeAuth,
        long timeExec,
        String accessDenied
) {

}