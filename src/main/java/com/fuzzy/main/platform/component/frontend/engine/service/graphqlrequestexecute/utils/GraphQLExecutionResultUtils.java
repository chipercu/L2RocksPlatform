package com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.utils;

import com.fuzzy.main.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.exception.runtime.PlatformRuntimeException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQLExecutionResultUtils {


    //Формируем пути по которым происходили access_denied
    public static String getAccessDenied(GExecutionResult executionResult, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (executionResult.getErrors() == null || executionResult.getErrors().isEmpty()) {
            return null;
        }

        Map<String, Integer> errors = new HashMap<>();
        for (GraphQLError graphQLError : executionResult.getErrors()) {
            if (!(graphQLError instanceof ExceptionWhileDataFetching)) {
                continue;
            }

            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable exception = exceptionWhileDataFetching.getException();
            if (!(exception instanceof PlatformRuntimeException)) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), exception);
                return null;
            }
            PlatformRuntimeException subsystemRuntimeException = (PlatformRuntimeException) exception;
            PlatformException subsystemException = subsystemRuntimeException.getPlatformException();

            if (subsystemException.getCode().equals(GeneralExceptionBuilder.ACCESS_DENIED_CODE)) {
                String path = "/" + graphQLError.getPath().stream()
                        .filter(o -> (o instanceof String)).map(o -> (String) o)
                        .collect(Collectors.joining("/"));

                errors.compute(path, (s, integer) -> (integer == null) ? 1 : integer + 1);
            }
        }
        if (errors.isEmpty()) {
            return null;
        }

        return errors.entrySet().stream().map(entry -> entry.getKey() + " (" + entry.getValue() + ")").collect(Collectors.joining(", "));
    }
}
