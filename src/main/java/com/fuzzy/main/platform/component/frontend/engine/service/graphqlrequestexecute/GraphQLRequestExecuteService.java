package com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute;

import com.fuzzy.main.cluster.graphql.GraphQLEngine;
import com.fuzzy.main.cluster.graphql.executor.GraphQLExecutorPrepareImpl;
import com.fuzzy.main.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.main.cluster.graphql.executor.struct.GSourceLocation;
import com.fuzzy.main.cluster.graphql.executor.struct.GSubscriptionPublisher;
import com.fuzzy.main.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.fuzzy.main.cluster.graphql.schema.GraphQLSchemaType;
import com.fuzzy.main.cluster.graphql.schema.scalartype.GraphQLTypeScalar;
import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.impl.ContextTransactionRequestImpl;
import com.fuzzy.main.platform.component.frontend.context.source.impl.SourceGRequestAuthImpl;
import com.fuzzy.main.platform.component.frontend.engine.authorize.RequestAuthorize;
import com.fuzzy.main.platform.component.frontend.engine.graphql.PrepareGraphQLDocument;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GExecutionStatistics;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.utils.GraphQLExecutionResultUtils;
import com.fuzzy.main.platform.exception.GraphQLWrapperPlatformException;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.exception.runtime.PlatformRuntimeException;
import com.fuzzy.main.platform.querypool.*;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.platform.sdk.graphql.out.GOutputFile;
import com.fuzzy.main.platform.utils.ExceptionUtils;
import graphql.*;
import graphql.execution.ExecutionId;
import graphql.execution.NonNullableValueCoercedAsNullException;
import graphql.language.SourceLocation;
import graphql.schema.CoercingParseValueException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GraphQLRequestExecuteService {

    private final static Logger log = LoggerFactory.getLogger(GraphQLRequestExecuteService.class);

    public final static String JSON_PROP_DATA = "data";
    public final static String JSON_PROP_ERROR = "error";

    private final Component frontendComponent;
    private final QueryPool queryPool;
    private final GraphQLEngine graphQLEngine;
    private final GraphQLExecutorPrepareImpl graphQLExecutorPrepare;
    private final RequestAuthorize.Builder requestAuthorizeBuilder;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public GraphQLRequestExecuteService(
            Component frontendComponent, QueryPool queryPool,
            GraphQLEngine graphQLEngine, GraphQLSubscribeEngine graphQLSubscribeEngine,
            RequestAuthorize.Builder requestAuthorizeBuilder,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        this.frontendComponent = frontendComponent;
        this.queryPool = queryPool;
        this.graphQLEngine = graphQLEngine;
        this.graphQLExecutorPrepare = (GraphQLExecutorPrepareImpl) graphQLEngine.buildExecutor(frontendComponent, graphQLSubscribeEngine);
        this.requestAuthorizeBuilder = requestAuthorizeBuilder;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public CompletableFuture<GraphQLResponse> execute(GRequest gRequest) {

        SourceGRequestAuthImpl source = new SourceGRequestAuthImpl(gRequest);
        ContextTransactionRequestImpl context = new ContextTransactionRequestImpl(source);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .executionId(ExecutionId.generate())
                .query(gRequest.getQuery())
                .operationName(gRequest.getOperationName())
                .context(context)
                .variables(Collections.unmodifiableMap(gRequest.getQueryVariables()))
                .build();

        //Парсим graphql запрос - собирая ресурсы
        PrepareGraphQLDocument prepareGraphQLDocument;
        try {
            prepareGraphQLDocument = new PrepareGraphQLDocument(graphQLExecutorPrepare, executionInput);
        } catch (Throwable throwable) {
            graphQLExecutorPrepare.requestCompleted(context);

            GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLPlatformException(throwable);
            return CompletableFuture.completedFuture(buildResponse(graphQLSubsystemException));
        }
        if (prepareGraphQLDocument.getPrepareDocumentRequest().preparsedDocumentEntry.hasErrors()) {//Произошла ошибка парсинга
            graphQLExecutorPrepare.requestCompleted(context);

            GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLPlatformException(
                    prepareGraphQLDocument.getPrepareDocumentRequest().preparsedDocumentEntry.getErrors().get(0),
                    null
            );
            return CompletableFuture.completedFuture(buildResponse(graphQLSubsystemException));
        }

        //Выполняем graphql запрос
        if (prepareGraphQLDocument.isQueryPoolRequest()) {
            return queryPool.execute(
                    frontendComponent,
                    context,
                    new Query<GraphQLResponse>() {

                        private QueryPool.Priority priority;

                        private RequestAuthorize requestAuthorize;

                        @Override
                        public void prepare(ResourceProvider resources) throws PlatformException {
                            requestAuthorize = requestAuthorizeBuilder.build(frontendComponent, gRequest, resources);

                            priority = requestAuthorize.getRequestPriority();

                            //Лочим ресурсы
                            ResourceProviderImpl resourceProvider = (ResourceProviderImpl) resources;
                            prepareGraphQLDocument.getWaitLockResources().forEach((resource, lockType) -> resourceProvider.borrowResource(resource, lockType));
                        }

                        public QueryPool.Priority getPriority() {
                            return priority;
                        }

                        @Override
                        public GraphQLResponse execute(QueryTransaction transaction) throws PlatformException {
                            Instant instantStartExecute = Instant.now();

                            UnauthorizedContext authContext = requestAuthorize.authorize(context);
                            source.setAuthContext(authContext);

                            Instant instantAuthorize = Instant.now();

                            GExecutionResult executionResult = graphQLExecutorPrepare.execute(prepareGraphQLDocument.getPrepareDocumentRequest());

                            GExecutionStatistics statistics = new GExecutionStatistics(
                                    authContext,
                                    priority,
                                    instantStartExecute.toEpochMilli() - gRequest.getInstant().toEpochMilli(),
                                    instantAuthorize.toEpochMilli() - instantStartExecute.toEpochMilli(),
                                    Instant.now().toEpochMilli() - instantStartExecute.toEpochMilli(),
                                    GraphQLExecutionResultUtils.getAccessDenied(executionResult, uncaughtExceptionHandler)
                            );

                            //Все чистим
                            graphQLExecutorPrepare.requestCompleted(context);

                            if (isExceptionWithIgnoreAccessDenied(executionResult)) {
                                //Необходимо обязательно кидать exception, что бы транзакция откатилась
                                throw coercionGraphQLPlatformException(executionResult.getErrors().get(0), statistics);
                            } else {
                                return buildResponse(executionResult, statistics);
                            }
                        }
                    }
            ).exceptionally(throwable -> {
                //Все чистим
                graphQLExecutorPrepare.requestCompleted(context);

                GraphQLWrapperPlatformException graphQLPlatformException = coercionGraphQLPlatformException(throwable);
                return buildResponse(graphQLPlatformException);
            });
        } else {
            try {
                Instant instantStartExecute = Instant.now();

                GExecutionResult executionResult = graphQLExecutorPrepare.execute(prepareGraphQLDocument.getPrepareDocumentRequest());

                GExecutionStatistics statistics = new GExecutionStatistics(
                        new UnauthorizedContext(),
                        null,
                        instantStartExecute.toEpochMilli() - gRequest.getInstant().toEpochMilli(),
                        0,
                        Instant.now().toEpochMilli() - instantStartExecute.toEpochMilli(),
                        GraphQLExecutionResultUtils.getAccessDenied(executionResult, uncaughtExceptionHandler)
                );

                GraphQLResponse<JSONObject> response;
                if (!executionResult.getErrors().isEmpty()) {
                    GraphQLWrapperPlatformException graphQLPlatformException = coercionGraphQLPlatformException(executionResult.getErrors().get(0), statistics);
                    response = buildResponse(graphQLPlatformException);
                } else {
                    response = buildResponse(executionResult, statistics);
                }
                return CompletableFuture.completedFuture(response);
            } catch (Throwable throwable) {
                GraphQLWrapperPlatformException graphQLSubsystemException = coercionGraphQLPlatformException(throwable);
                return CompletableFuture.completedFuture(buildResponse(graphQLSubsystemException));
            } finally {
                graphQLExecutorPrepare.requestCompleted(context);
            }
        }
    }

    private boolean isExceptionWithIgnoreAccessDenied(GExecutionResult executionResult) {
        if (executionResult.getErrors().isEmpty()) return false;
        if (executionResult.getData() == null) {
            return true;//Хак. Необходимо более глубокое иследование - это надо для подписок - когда не удалось выполнить подписку, иначе агент не узнает о ошибке подписки
        }
        for (GraphQLError graphQLError : executionResult.getErrors()) {
            if (!(graphQLError instanceof ExceptionWhileDataFetching)) return true;
            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable throwable = exceptionWhileDataFetching.getException();

            PlatformException subsystemException = null;
            if (throwable instanceof PlatformException) {
                subsystemException = (PlatformException) throwable;
            } else if (throwable instanceof PlatformRuntimeException) {
                subsystemException = ((PlatformRuntimeException) throwable).getPlatformException();
            }
            if (subsystemException == null) return true;
            if (!subsystemException.getCode().equals(GeneralExceptionBuilder.ACCESS_DENIED_CODE)) return true;
        }
        return false;
    }

    public static GraphQLWrapperPlatformException coercionGraphQLPlatformException(Throwable throwable) {
        if (throwable instanceof GraphQLWrapperPlatformException) {
            return (GraphQLWrapperPlatformException) throwable;
        } else if (throwable instanceof PlatformException) {
            return new GraphQLWrapperPlatformException((PlatformException) throwable);
        } else if (throwable instanceof PlatformRuntimeException) {
            return new GraphQLWrapperPlatformException(((PlatformRuntimeException) throwable).getPlatformException());
        } else if (throwable instanceof AssertException
                || throwable instanceof CoercingParseValueException
                || throwable instanceof NonNullableValueCoercedAsNullException
        ) {
            List<GSourceLocation> locations = null;
            if (throwable instanceof GraphQLError graphQLError) {
                locations = convertLocation(graphQLError);
            }
            return new GraphQLWrapperPlatformException(
                    GeneralExceptionBuilder.buildGraphQLValidationException(throwable.getMessage()),
                    locations,
                    null
            );
        } else {
            throw ExceptionUtils.coercionRuntimeException(throwable);
        }
    }

    public GraphQLResponse<JSONObject> buildResponse(GraphQLWrapperPlatformException graphQLPlatformException) {
        PlatformException e = graphQLPlatformException.getPlatformException();
        List<GSourceLocation> sourceLocations = graphQLPlatformException.getSourceLocations();

        JSONObject error = new JSONObject();

        if (e.getComponentUuid() != null) {
            error.put("subsystem_uuid", e.getComponentUuid());
        }

        error.put("code", e.getCode());

        if (e.getParameters() != null && !e.getParameters().isEmpty()) {
            GraphQLSchemaType graphQLSchemaType = graphQLEngine.getGraphQLSchemaType();

            JSONObject outParameters = new JSONObject();
            for (Map.Entry<String, Object> entry : e.getParameters().entrySet()) {
                outParameters.put(entry.getKey(), buildJSONParameter(graphQLSchemaType, entry.getValue()));
            }
            error.put("parameters", outParameters);
        }

        if (sourceLocations != null) {
            JSONArray locations = new JSONArray();
            for (GSourceLocation sourceLocation : sourceLocations) {
                locations.add(new JSONObject()
                        .appendField("line", sourceLocation.getLine())
                        .appendField("column", sourceLocation.getColumn())
                );
            }
            error.put("source_location", locations);
        }

        return new GraphQLResponse<>(error, true, graphQLPlatformException.getStatistics());
    }

    private static GraphQLWrapperPlatformException coercionGraphQLPlatformException(GraphQLError graphQLError, GExecutionStatistics statistics) {
        ErrorClassification errorType = graphQLError.getErrorType();

        PlatformException platformException;
        if (errorType == ErrorType.InvalidSyntax) {
            platformException = GeneralExceptionBuilder.buildGraphQLInvalidSyntaxException();
        } else if (errorType == ErrorType.ValidationError) {
            platformException = GeneralExceptionBuilder.buildGraphQLValidationException();
        } else if (errorType == ErrorType.DataFetchingException) {
            ExceptionWhileDataFetching exceptionWhileDataFetching = (ExceptionWhileDataFetching) graphQLError;
            Throwable dataFetchingThrowable = exceptionWhileDataFetching.getException();
            if (dataFetchingThrowable instanceof PlatformRuntimeException) {
                platformException = ((PlatformRuntimeException) dataFetchingThrowable).getPlatformException();
            } else if (dataFetchingThrowable instanceof PlatformException) {
                platformException = (PlatformException) dataFetchingThrowable;
            } else {
                throw ExceptionUtils.coercionRuntimeException(dataFetchingThrowable);
            }
        } else {
            throw new RuntimeException("Not support error type: " + graphQLError.getErrorType());
        }
        return new GraphQLWrapperPlatformException(platformException, convertLocation(graphQLError), statistics);
    }

    public static GraphQLResponse buildResponse(GExecutionResult executionResult, GExecutionStatistics statistics) {
        GOutputFile gOutputFile = findOutputFile(executionResult.getData());
        if (gOutputFile != null) {
            return new GraphQLResponse(gOutputFile, false, statistics);
        } else {
            if (executionResult.getData() instanceof GSubscriptionPublisher) {
                return new GraphQLResponse(executionResult.getData(), false, statistics);
            } else {
                return new GraphQLResponse(new JSONObject(executionResult.getData()), false, statistics);
            }
        }
    }

    private static GOutputFile findOutputFile(Object response) {
        if (response instanceof Map) {
            for (Object oEntry : ((Map) response).entrySet()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) oEntry;
                GOutputFile iFindOutputFile = findOutputFile(entry.getValue());
                if (iFindOutputFile != null) return iFindOutputFile;
            }
        } else if (response instanceof GOutputFile) {
            return (GOutputFile) response;
        }
        return null;
    }

    private static Object buildJSONParameter(GraphQLSchemaType graphQLSchemaType, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Map) {
            JSONObject outObject = new JSONObject();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                outObject.put(entry.getKey(), buildJSONParameter(graphQLSchemaType, entry.getValue()));
            }
            return outObject;
        } else if (value instanceof Collection) {
            JSONArray outArray = new JSONArray();
            for (Object item : (Collection) value) {
                outArray.add(buildJSONParameter(graphQLSchemaType, item));
            }
            return outArray;
        } else if (value instanceof Enum) {
            return ((Enum) value).name();
        } else {
            GraphQLTypeScalar graphQLTypeScalar = graphQLSchemaType.getTypeScalarByClass(value.getClass());
            if (graphQLTypeScalar == null) {
                throw new RuntimeException("Not support type object(not scalar): " + value);
            }
            return graphQLTypeScalar.getGraphQLScalarType().getCoercing().serialize(value);
        }
    }

    private static List<GSourceLocation> convertLocation(GraphQLError graphQLError) {
        List<SourceLocation> nLocations = graphQLError.getLocations();
        if (nLocations != null) {
            return nLocations.stream().map(GSourceLocation::new).toList();
        } else {
            return null;
        }
    }
}
