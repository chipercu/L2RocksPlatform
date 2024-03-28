package com.fuzzy.main.cluster.graphql.executor.struct;

import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.execution.reactive.SubscriptionPublisher;

import java.util.List;

public class GExecutionResult {

    private final ExecutionResult executionResult;

    public GExecutionResult(ExecutionResult executionResult) {
        this.executionResult = executionResult;
    }

    public <T> T getData() {
        if (executionResult.getData() instanceof SubscriptionPublisher sp) {
            return (T) new GSubscriptionPublisher(sp);
        } else {
            return executionResult.getData();
        }
    }

    public List<GraphQLError> getErrors() {
        return executionResult.getErrors();
    }

}
