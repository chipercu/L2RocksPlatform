package com.fuzzy.main.cluster.graphql.exception;

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
