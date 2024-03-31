package com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct;

import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GExecutionStatistics;

public class GraphQLResponse<T> {

    public final T data;
    public final boolean error;

    public final GExecutionStatistics statistics;

    public GraphQLResponse(
            T data, boolean error,
            GExecutionStatistics statistics
    ) {
        this.data = data;
        this.error = error;
        this.statistics = statistics;
    }

}
