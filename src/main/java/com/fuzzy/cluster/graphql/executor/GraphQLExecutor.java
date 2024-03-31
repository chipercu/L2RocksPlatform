package com.fuzzy.cluster.graphql.executor;

import com.fuzzy.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.cluster.graphql.struct.ContextRequest;
import graphql.ExecutionInput;

public interface GraphQLExecutor {

    GExecutionResult execute(ExecutionInput executionInput);

    void requestCompleted(ContextRequest context);

}
