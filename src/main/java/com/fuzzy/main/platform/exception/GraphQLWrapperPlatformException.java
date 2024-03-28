package com.fuzzy.main.platform.exception;

import com.fuzzy.main.cluster.graphql.executor.struct.GSourceLocation;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GExecutionStatistics;

import java.util.List;

public class GraphQLWrapperPlatformException extends PlatformException {

    private List<GSourceLocation> sourceLocations;

    private final GExecutionStatistics statistics;

    public GraphQLWrapperPlatformException(PlatformException platformException) {
        this(platformException, null, null);
    }

    public GraphQLWrapperPlatformException(PlatformException subsystemException, List<GSourceLocation> sourceLocations, GExecutionStatistics statistics) {
        super("wrapper", null, null, subsystemException);
        this.sourceLocations = sourceLocations;
        this.statistics = statistics;
    }

    public PlatformException getPlatformException() {
        return (PlatformException) getCause();
    }

    public List<GSourceLocation> getSourceLocations() {
        return sourceLocations;
    }

    public GExecutionStatistics getStatistics() {
        return statistics;
    }
}
