package com.fuzzy.cluster.graphql.exception;

public class GraphQLExecutorException extends RuntimeException {

    public GraphQLExecutorException() {
        super();
    }

    public GraphQLExecutorException(String message) {
        super(message);
    }

    public GraphQLExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphQLExecutorException(Throwable cause) {
        super(cause);
    }
}
