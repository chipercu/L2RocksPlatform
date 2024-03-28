package com.fuzzy.main.cluster.graphql.executor;

import com.fuzzy.main.cluster.core.remote.RemoteTarget;
import com.fuzzy.main.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.fuzzy.main.cluster.exception.ClusterRemotePackerException;
import com.fuzzy.main.cluster.graphql.exception.GraphQLExecutorDataFetcherException;
import com.fuzzy.main.cluster.graphql.exception.GraphQLExecutorInvalidSyntaxException;
import com.fuzzy.main.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.main.cluster.graphql.preparecustomfield.PrepareCustomField;
import com.fuzzy.main.cluster.graphql.preparecustomfield.PrepareCustomFieldUtils;
import com.fuzzy.main.cluster.graphql.remote.graphql.executor.RControllerGraphQLExecutor;
import com.fuzzy.main.cluster.graphql.schema.GraphQLSchemaType;
import com.fuzzy.main.cluster.graphql.schema.build.MergeGraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.schema.build.MergeGraphQLTypeOutObjectInterface;
import com.fuzzy.main.cluster.graphql.schema.datafetcher.ComponentDataFetcher;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import com.fuzzy.main.cluster.graphql.utils.ExceptionUtils;
import com.fuzzy.main.cluster.struct.Component;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.InvalidSyntaxError;
import graphql.execution.CoercedVariables;
import graphql.execution.ValuesResolver;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.introspection.Introspection;
import graphql.language.*;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;


// Основная идея это разрезать метод parseValidateAndExecute на 2 части и через грязные хаки вызвать их отдельно

// private CompletableFuture<ExecutionResult> parseValidateAndExecute(ExecutionInput executionInput, GraphQLSchema graphQLSchema, InstrumentationState instrumentationState) {
// PreparsedDocumentEntry preparsedDoc = preparsedDocumentProvider.get(executionInput.getQuery(), query -> parseAndValidate(executionInput, graphQLSchema, instrumentationState));

// if (preparsedDoc.hasErrors()) {
// return CompletableFuture.completedFuture(new ExecutionResultImpl(preparsedDoc.getErrors()));
// }

// return execute(executionInput, preparsedDoc.getDocument(), graphQLSchema, instrumentationState);
// }

public class GraphQLExecutorPrepareImpl implements GraphQLExecutor {

    private final static Logger log = LoggerFactory.getLogger(GraphQLExecutorPrepareImpl.class);

    private final static String GRAPHQL_TYPE = "__Type";
    private final static String GRAPHQL_INPUT_VALUE = "__InputValue";
    private final static String GRAPHQL_FIELD_SCHEME = "__schema";
    private final static String GRAPHQL_FIELD_TYPENAME = "__typename";

    @FunctionalInterface
    public interface PrepareFunction<T extends Serializable> {
        void prepare(RGraphQLObjectTypeField rGraphQLObjectTypeField, T t);
    }

    public class PrepareDocumentRequest {

        public final ExecutionInput executionInput;
        public final PreparsedDocumentEntry preparsedDocumentEntry;
        public final InstrumentationState instrumentationState;

        public PrepareDocumentRequest(ExecutionInput executionInput, PreparsedDocumentEntry preparsedDocumentEntry, InstrumentationState instrumentationState) {
            this.executionInput = executionInput;
            this.preparsedDocumentEntry = preparsedDocumentEntry;
            this.instrumentationState = instrumentationState;
        }
    }

    private final Component component;
    private final GraphQLSchema schema;
    private final GraphQL graphQL;
    private final GraphQLSchemaType graphQLSchemaType;
    private final Instrumentation instrumentation;
    private final PreparsedDocumentProvider preparsedDocumentProvider;
    private final Method methodParseAndValidate;
    private final Method methodExecute;
    private final Map<String, MergeGraphQLTypeOutObject> remoteGraphQLTypeOutObjects;
    private final Map<String, MergeGraphQLTypeOutObjectInterface> remoteGraphQLTypeOutObjectInterfaces;

    public GraphQLExecutorPrepareImpl(Component component, GraphQLSchema schema, GraphQL graphQL, Map<String, MergeGraphQLTypeOutObject> remoteGraphQLTypeOutObjects, Map<String, MergeGraphQLTypeOutObjectInterface> remoteGraphQLTypeOutObjectInterfaces, GraphQLSchemaType graphQLSchemaType) {
        this.component = component;
        this.schema = schema;
        this.graphQL = graphQL;
        this.remoteGraphQLTypeOutObjects = remoteGraphQLTypeOutObjects;
        this.remoteGraphQLTypeOutObjectInterfaces = remoteGraphQLTypeOutObjectInterfaces;
        this.graphQLSchemaType = graphQLSchemaType;

        try {
            Field fieldInstrumentation = graphQL.getClass().getDeclaredField("instrumentation");
            fieldInstrumentation.setAccessible(true);
            instrumentation = (Instrumentation) fieldInstrumentation.get(graphQL);

            Field fieldPreparsedDocumentProvider = graphQL.getClass().getDeclaredField("preparsedDocumentProvider");
            fieldPreparsedDocumentProvider.setAccessible(true);
            preparsedDocumentProvider = (PreparsedDocumentProvider) fieldPreparsedDocumentProvider.get(graphQL);

            methodParseAndValidate = graphQL.getClass().getDeclaredMethod("parseAndValidate", AtomicReference.class, GraphQLSchema.class, InstrumentationState.class);
            methodParseAndValidate.setAccessible(true);

            methodExecute = graphQL.getClass().getDeclaredMethod("execute", ExecutionInput.class, Document.class, GraphQLSchema.class, InstrumentationState.class);
            methodExecute.setAccessible(true);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Изменилась реализация библиотеки GraphQL", e);
        }
    }

    public GraphQLSchema getSchema() {
        return schema;
    }

    public PrepareDocumentRequest prepare(ExecutionInput executionInput, PrepareFunction prepareFunction) throws GraphQLExecutorDataFetcherException {
        if (executionInput.getExecutionId() == null) {
            throw new RuntimeException("You must provide a query identifier");
        }

        //Код вырезан из: GraphQL.executeAsync(ExecutionInput executionInput)
        InstrumentationState instrumentationState = instrumentation.createState(new InstrumentationCreateStateParameters(schema, executionInput));

        InstrumentationExecutionParameters inputInstrumentationParameters = new InstrumentationExecutionParameters(executionInput, schema, instrumentationState);
        executionInput = instrumentation.instrumentExecutionInput(executionInput, inputInstrumentationParameters);

        InstrumentationExecutionParameters instrumentationParameters = new InstrumentationExecutionParameters(executionInput, schema, instrumentationState);
        instrumentation.beginExecution(instrumentationParameters);

        AtomicReference<ExecutionInput> executionInputRef = new AtomicReference<>(executionInput);
        PreparsedDocumentEntry preparsedDocumentEntry = preparsedDocumentProvider.getDocument(executionInput, function -> {
            try {
                return (PreparsedDocumentEntry) methodParseAndValidate.invoke(graphQL, executionInputRef, schema, instrumentationState);
            } catch (InvocationTargetException ite) {
                throw ExceptionUtils.coercionRuntimeException(ite.getTargetException());
            } catch (Throwable e) {
                throw new RuntimeException("Изменилась реализация библиотеки GraphQL", e);
            }
        });

        if (preparsedDocumentEntry.hasErrors()) {
            //Произошла ошибка парсинга
            return new PrepareDocumentRequest(
                    executionInput,
                    preparsedDocumentEntry,
                    instrumentationState
            );
        }

        //Документ распарсен - вызываем prepare
        try {
            Document document = preparsedDocumentEntry.getDocument();
            for (Node node : document.getChildren()) {
                if (node instanceof OperationDefinition) {
                    OperationDefinition operationDefinition = (OperationDefinition) node;

                    GraphQLObjectType parent;
                    if (operationDefinition.getOperation() == OperationDefinition.Operation.QUERY) {
                        parent = schema.getQueryType();
                    } else if (operationDefinition.getOperation() == OperationDefinition.Operation.MUTATION) {
                        parent = schema.getMutationType();
                    } else if (operationDefinition.getOperation() == OperationDefinition.Operation.SUBSCRIPTION) {
                        parent = schema.getSubscriptionType();
                    } else {
                        throw new RuntimeException("not support operation type: " + operationDefinition.getOperation());
                    }

                    prepareRequest(
                            parent,
                            node,
                            executionInput.getVariables(),
                            prepareFunction,
                            (ContextRequest) executionInput.getContext()
                    );
                } else if (node instanceof FragmentDefinition) {
                    FragmentDefinition fragmentDefinition = (FragmentDefinition) node;

                    GraphQLObjectType parent = schema.getObjectType(fragmentDefinition.getTypeCondition().getName());
                    prepareRequest(
                            parent,
                            node,
                            executionInput.getVariables(),
                            prepareFunction,
                            (ContextRequest) executionInput.getContext()
                    );
                }
            }

            return new PrepareDocumentRequest(
                    executionInput,
                    preparsedDocumentEntry,
                    instrumentationState
            );
        } catch (GraphQLExecutorInvalidSyntaxException e) {
            //Произошла ошибка парсинга
            return new PrepareDocumentRequest(
                    executionInput,
                    new PreparsedDocumentEntry(new InvalidSyntaxError(
                            new SourceLocation(0, 0),
                            e.getMessage())),
                    instrumentationState
            );
        } catch (GraphQLExecutorDataFetcherException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Not support exception", e);
        }
    }

    public GExecutionResult execute(PrepareDocumentRequest prepareDocumentRequest) {
        try {
            CompletableFuture<ExecutionResult> completableFuture = (CompletableFuture<ExecutionResult>) methodExecute.invoke(graphQL,
                    prepareDocumentRequest.executionInput,
                    prepareDocumentRequest.preparsedDocumentEntry.getDocument(),
                    schema,
                    prepareDocumentRequest.instrumentationState);

            return new GExecutionResult(completableFuture.join());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Изменилась реализация библиотеки GraphQL", e);
        }
    }

    @Override
    public GExecutionResult execute(ExecutionInput executionInput) {
        return new GExecutionResult(graphQL.execute(executionInput));
    }

    @Override
    public void requestCompleted(ContextRequest context) {
        //Ulitin V. В будущем необходимо решить вопрос с удаленым вызовым
        for (PrepareCustomField prepareCustomField : graphQLSchemaType.prepareCustomFields) {
            prepareCustomField.requestCompleted(context);
        }
    }

    private void prepareRequest(GraphQLType parent, Node node, Map<String, Object> variables, PrepareFunction prepareFunction, ContextRequest context) throws Exception {
//        String parentName1;
//        if (parent instanceof GraphQLObjectType) {
//            GraphQLObjectType parentGraphQLObjectType = (GraphQLObjectType) parent;
//            parentName1 = parentGraphQLObjectType.getName();
//        } else {
//            parentName1 = "";
//        }
//
//        if (GRAPHQL_TYPE.equals(parentName1)) return;
//        if (GRAPHQL_INPUT_VALUE.equals(parentName1)) return;

        if (node instanceof graphql.language.Field) {
            graphql.language.Field field = (graphql.language.Field) node;
            if (GRAPHQL_FIELD_SCHEME.equals(field.getName())) return;
            if (GRAPHQL_FIELD_TYPENAME.equals(field.getName())) return;

            GraphQLNamedSchemaElement parentGraphQLNamedSchemaElement = (GraphQLNamedSchemaElement) parent;
            String parentName = parentGraphQLNamedSchemaElement.getName();

            RGraphQLObjectTypeField rGraphQLObjectTypeField = null;
            MergeGraphQLTypeOutObject mergeGraphQLTypeOutObject = remoteGraphQLTypeOutObjects.get(parentName);
            MergeGraphQLTypeOutObjectInterface mergeGraphQLTypeOutObjectInterface = remoteGraphQLTypeOutObjectInterfaces.get(parentName);
            if (mergeGraphQLTypeOutObject != null) {
                rGraphQLObjectTypeField = mergeGraphQLTypeOutObject.getFieldByExternalName(field.getName());
            } else if (mergeGraphQLTypeOutObjectInterface != null) {
                rGraphQLObjectTypeField = mergeGraphQLTypeOutObjectInterface.getFieldByExternalName(field.getName());
            }
            if (rGraphQLObjectTypeField == null) {
                return;
            }

            if (rGraphQLObjectTypeField.isPrepare) {
                HashMap<String, Serializable> arguments = ComponentDataFetcher.filterArguments(
                        field,
                        ValuesResolver.getArgumentValues(
                                schema.getCodeRegistry(),
                                Introspection.getFieldDef(schema, (GraphQLCompositeType) parent, field.getName()).getArguments(),
                                field.getArguments(),
                                CoercedVariables.of(variables)
                        ),
                        variables.keySet()
                );

                //Собираем какие ресурсы нам необходимы для лока
                LocationRuntimeComponent runtimeComponentInfo = component.getTransport().getNetworkTransit().getManagerRuntimeComponent().get(rGraphQLObjectTypeField.nodeRuntimeId, rGraphQLObjectTypeField.componentId);
                if (runtimeComponentInfo == null) {
                    throw new ClusterRemotePackerException();
                }
                RemoteTarget target = new RemoteTarget(rGraphQLObjectTypeField.nodeRuntimeId, rGraphQLObjectTypeField.componentId, runtimeComponentInfo.component().uuid);

                RControllerGraphQLExecutor rControllerGraphQLExecutor = component.getRemotes().getFromCKey(target, RControllerGraphQLExecutor.class);
                Serializable prepareRequest = rControllerGraphQLExecutor.prepare(
                        PrepareCustomFieldUtils.getKeyField(field),
                        parentName,
                        rGraphQLObjectTypeField.name,
                        arguments,
                        context
                );
                prepareFunction.prepare(rGraphQLObjectTypeField, prepareRequest);
            } else {
                prepareFunction.prepare(rGraphQLObjectTypeField, null);
            }

            for (Node iNode : field.getChildren()) {
                if (parent instanceof GraphQLFieldsContainer) {
                    prepareRequest(((GraphQLFieldsContainer) parent).getFieldDefinition(field.getName()).getType(), iNode, variables, prepareFunction, context);
                } else if (parent instanceof GraphQLList) {
                    prepareRequest(parent, iNode, variables, prepareFunction, context);
                } else {
                    throw new RuntimeException("not support parent type: " + parent);
                }
            }
        } else if (node instanceof SelectionSet) {
            SelectionSet selectionSetNode = (SelectionSet) node;
            for (Node iNode : selectionSetNode.getChildren()) {
                if (parent instanceof GraphQLList) {
                    prepareRequest(((GraphQLList) parent).getWrappedType(), iNode, variables, prepareFunction, context);
                } else {
                    prepareRequest(parent, iNode, variables, prepareFunction, context);
                }
            }
        } else if (node instanceof OperationDefinition) {
            OperationDefinition operationDefinitionNode = (OperationDefinition) node;
            for (Node iNode : operationDefinitionNode.getChildren()) {
                prepareRequest(parent, iNode, variables, prepareFunction, context);
            }
        } else if (node instanceof FragmentDefinition) {
            FragmentDefinition fragmentDefinition = (FragmentDefinition) node;
            for (Node iNode : fragmentDefinition.getChildren()) {
                prepareRequest(parent, iNode, variables, prepareFunction, context);
            }
        } else if (node instanceof InlineFragment) {
            InlineFragment inlineFragment = (InlineFragment) node;
            for (Node iNode : inlineFragment.getChildren()) {
                prepareRequest(schema.getObjectType(inlineFragment.getTypeCondition().getName()), iNode, variables, prepareFunction, context);
            }
        }
    }
}
