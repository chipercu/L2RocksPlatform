package com.fuzzy.platform.exception;

import com.fuzzy.cluster.graphql.executor.struct.GSourceLocation;
import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GExecutionStatistics;
import com.fuzzy.platform.exception.PlatformException;

import java.util.List;

public class GraphQLWrapperPlatformException extends com.fuzzy.platform.exception.PlatformException {

    private List<GSourceLocation> sourceLocations;

    private final GExecutionStatistics statistics;

    public GraphQLWrapperPlatformException(com.fuzzy.platform.exception.PlatformException platformException) {
        this(platformException, null, null);
    }

    public GraphQLWrapperPlatformException(com.fuzzy.platform.exception.PlatformException subsystemException, List<GSourceLocation> sourceLocations, GExecutionStatistics statistics) {
        super("wrapper", null, null, subsystemException);
        this.sourceLocations = sourceLocations;
        this.statistics = statistics;
    }

    public com.fuzzy.platform.exception.PlatformException getPlatformException() {
        return (PlatformException) getCause();
    }

    public List<GSourceLocation> getSourceLocations() {
        return sourceLocations;
    }

    public GExecutionStatistics getStatistics() {
        return statistics;
    }
}
