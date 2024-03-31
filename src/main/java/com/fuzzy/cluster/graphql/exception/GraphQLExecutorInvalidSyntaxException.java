package com.fuzzy.cluster.graphql.exception;

import com.fuzzy.cluster.graphql.exception.GraphQLExecutorException;

public class GraphQLExecutorInvalidSyntaxException extends GraphQLExecutorException {

    public GraphQLExecutorInvalidSyntaxException() {
        super();
    }

    public GraphQLExecutorInvalidSyntaxException(String message) {
        super(message);
    }

    public GraphQLExecutorInvalidSyntaxException(Throwable cause) {
        super(cause);
    }
}
