package com.fuzzy.main.cluster.graphql.executor;

import com.fuzzy.main.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import graphql.ExecutionInput;

public interface GraphQLExecutor {

    GExecutionResult execute(ExecutionInput executionInput);

    void requestCompleted(ContextRequest context);

}
