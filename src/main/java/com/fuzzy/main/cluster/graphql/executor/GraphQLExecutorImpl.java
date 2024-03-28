package com.fuzzy.main.cluster.graphql.executor;

import com.fuzzy.main.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

public class GraphQLExecutorImpl implements GraphQLExecutor {

    private final GraphQLSchema schema;
    private final GraphQL graphQL;

    public GraphQLExecutorImpl(GraphQLSchema schema, GraphQL graphQL) {
        this.schema = schema;
        this.graphQL = graphQL;
    }

    public GraphQLSchema getSchema() {
        return schema;
    }

    @Override
    public GExecutionResult execute(ExecutionInput executionInput) {
        return new GExecutionResult(graphQL.execute(executionInput));
    }

    @Override
    public void requestCompleted(ContextRequest context) {
    }
}
