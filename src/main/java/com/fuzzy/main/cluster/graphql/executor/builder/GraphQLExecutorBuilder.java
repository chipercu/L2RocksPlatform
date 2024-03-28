package com.fuzzy.main.cluster.graphql.executor.builder;

import com.fuzzy.main.cluster.graphql.exception.GraphQLExecutorException;
import com.fuzzy.main.cluster.graphql.executor.GraphQLExecutor;
import com.fuzzy.main.cluster.graphql.executor.GraphQLExecutorImpl;
import com.fuzzy.main.cluster.graphql.executor.GraphQLExecutorPrepareImpl;
import com.fuzzy.main.cluster.graphql.executor.component.GraphQLComponentExecutor;
import com.fuzzy.main.cluster.graphql.executor.subscription.GraphQLSubscribeEngineImpl;
import com.fuzzy.main.cluster.graphql.remote.graphql.executor.RControllerGraphQLExecutor;
import com.fuzzy.main.cluster.graphql.schema.GraphQLSchemaType;
import com.fuzzy.main.cluster.graphql.schema.build.MergeGraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.schema.build.MergeGraphQLTypeOutObjectInterface;
import com.fuzzy.main.cluster.graphql.schema.build.graphqltype.TypeGraphQLFieldConfigurationBuilder;
import com.fuzzy.main.cluster.graphql.schema.datafetcher.ComponentDataFetcher;
import com.fuzzy.main.cluster.graphql.schema.datafetcher.ExtPropertyDataFetcher;
import com.fuzzy.main.cluster.graphql.schema.scalartype.GraphQLTypeScalar;
import com.fuzzy.main.cluster.graphql.schema.struct.RGraphQLType;
import com.fuzzy.main.cluster.graphql.schema.struct.RGraphQLTypeEnum;
import com.fuzzy.main.cluster.graphql.schema.struct.in.RGraphQLInputObjectTypeField;
import com.fuzzy.main.cluster.graphql.schema.struct.in.RGraphQLTypeInObject;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLObjectTypeMethodArgument;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLTypeOutObjectInterface;
import com.fuzzy.main.cluster.graphql.utils.Utils;
import com.fuzzy.main.cluster.struct.Component;
import graphql.GraphQL;
import graphql.TypeResolutionEnvironment;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.*;

import java.lang.reflect.Constructor;
import java.util.*;

import static graphql.schema.GraphQLSchema.newSchema;

public class GraphQLExecutorBuilder {

    private final Component component;
    private final String sdkPackagePath;
    private final Constructor customRemoteDataFetcher;

    private final TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder;
    private final GraphQLSchemaType graphQLSchemaType;
    private final GraphQLSubscribeEngineImpl subscribeEngine;
    private final DataFetcherExceptionHandler dataFetcherExceptionHandler;

    private GraphQLComponentExecutor sdkGraphQLItemExecutor;

    public GraphQLExecutorBuilder(
            Component component,
            String sdkPackagePath,
            Constructor customRemoteDataFetcher,
            TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder,
            GraphQLSchemaType graphQLSchemaType,
            GraphQLSubscribeEngineImpl subscribeEngine,
            DataFetcherExceptionHandler dataFetcherExceptionHandler
    ) {
        this.component = component;
        this.sdkPackagePath = sdkPackagePath;
        this.customRemoteDataFetcher = customRemoteDataFetcher;
        this.fieldConfigurationBuilder = fieldConfigurationBuilder;
        this.graphQLSchemaType = graphQLSchemaType;
        this.subscribeEngine = subscribeEngine;
        this.dataFetcherExceptionHandler = dataFetcherExceptionHandler;
    }

    public GraphQLExecutor build() throws GraphQLExecutorException {
        try {

            //Собираем какие типы у нас вообще есть
            List<RGraphQLTypeEnum> buildGraphQLTypeEnums = new ArrayList<RGraphQLTypeEnum>();
            Map<String, MergeGraphQLTypeOutObject> buildGraphQLTypeOutObjects = new HashMap<String, MergeGraphQLTypeOutObject>();
            Map<String, MergeGraphQLTypeOutObjectInterface> buildGraphQLTypeOutObjectUnions = new HashMap<String, MergeGraphQLTypeOutObjectInterface>();
            Map<String, Set<RGraphQLInputObjectTypeField>> buildGraphQLTypeInObjects = new HashMap<String, Set<RGraphQLInputObjectTypeField>>();

            //Собираем встроенные
            if (sdkPackagePath != null) {
                sdkGraphQLItemExecutor = new GraphQLComponentExecutor(
                        sdkPackagePath, fieldConfigurationBuilder, graphQLSchemaType
                );
                for (RGraphQLType rGraphQLType : sdkGraphQLItemExecutor.getGraphQLTypes()) {
                    mergeGraphQLType(
                            buildGraphQLTypeEnums,
                            buildGraphQLTypeOutObjects,
                            buildGraphQLTypeOutObjectUnions,
                            buildGraphQLTypeInObjects,
                            rGraphQLType
                    );
                }
            }

            //Запрашиваем у подсистем
            Collection<RControllerGraphQLExecutor> rControllerGraphQLExecutors = component.getRemotes().getControllers(RControllerGraphQLExecutor.class);
            for (RControllerGraphQLExecutor rControllerGraphQLExecutor : rControllerGraphQLExecutors) {
                for (RGraphQLType rGraphQLType : rControllerGraphQLExecutor.getGraphQLTypes()) {
                    mergeGraphQLType(
                            buildGraphQLTypeEnums,
                            buildGraphQLTypeOutObjects,
                            buildGraphQLTypeOutObjectUnions,
                            buildGraphQLTypeInObjects,
                            rGraphQLType
                    );
                }
            }

            //В этот map добавляются все построенные типы
            Map<String, GraphQLType> graphQLTypes = new HashMap<String, GraphQLType>();

            //Добавляем все скаляры
            for (GraphQLTypeScalar graphQLScalarType : graphQLSchemaType.typeScalars) {
                String name = graphQLScalarType.getName();
                graphQLTypes.put(name, graphQLScalarType.getGraphQLScalarType());
                graphQLTypes.put("collection:" + name, new GraphQLList(graphQLScalarType.getGraphQLScalarType()));
            }

            //Добавляем все enum
            for (RGraphQLTypeEnum rGraphQLEnumType : buildGraphQLTypeEnums) {
                buildGraphQLTypeEnum(graphQLTypes, rGraphQLEnumType);
            }

            //Разбираемся с зависимостями input объектами
            for (Map.Entry<String, Set<RGraphQLInputObjectTypeField>> entry : buildGraphQLTypeInObjects.entrySet()) {
                String graphQLTypeName = entry.getKey();
                Set<RGraphQLInputObjectTypeField> graphQLTypeFields = entry.getValue();

                buildGraphQLTypeInObject(graphQLTypes, graphQLTypeName, graphQLTypeFields);
            }

            GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();

            //Разбираемся с зависимостями output объектов
            Map<String, MergeGraphQLTypeOutObject> waitBuildGraphQLTypeOutObjects = new HashMap<String, MergeGraphQLTypeOutObject>(buildGraphQLTypeOutObjects);
            while (!waitBuildGraphQLTypeOutObjects.isEmpty()) {
                boolean isCyclicDependency = true;
                for (MergeGraphQLTypeOutObject graphQLTypeOutObject : waitBuildGraphQLTypeOutObjects.values()) {

                    boolean isLoadedDependenciesType = true;
                    for (RGraphQLObjectTypeField typeGraphQLField : graphQLTypeOutObject.getFields()) {
                        String[] compositeTypes = typeGraphQLField.type.split(":");
                        for (String compositeType : compositeTypes) {
                            if ("collection".equals(compositeType)) continue;
                            if (!graphQLTypes.containsKey(compositeType)) {
                                isLoadedDependenciesType = false;
                            }
                        }
                    }

                    if (isLoadedDependenciesType) {
                        //Все зависимости-поля есть, можно загружать
                        buildGraphQLTypeOutObject(codeRegistryBuilder, graphQLTypes, graphQLTypeOutObject, buildGraphQLTypeOutObjectUnions);

                        //Загрузка прошла успешно
                        waitBuildGraphQLTypeOutObjects.remove(graphQLTypeOutObject.name);
                        isCyclicDependency = false;
                        break;
                    }
                }

                if (isCyclicDependency) {
                    //Блински циклическая зависимость, строим первую попавшую через ссылки
                    String graphQLTypeName = waitBuildGraphQLTypeOutObjects.keySet().iterator().next();
                    MergeGraphQLTypeOutObject graphQLTypeOutObject = waitBuildGraphQLTypeOutObjects.get(graphQLTypeName);

                    buildGraphQLTypeOutObject(codeRegistryBuilder, graphQLTypes, graphQLTypeOutObject, buildGraphQLTypeOutObjectUnions);

                    //Загрузка прошла успешно
                    waitBuildGraphQLTypeOutObjects.remove(graphQLTypeName);
                }
            }

            //Разбираемся с зависимостями output union объектов
            for (MergeGraphQLTypeOutObjectInterface mergeGraphQLTypeOutObjectUnion : buildGraphQLTypeOutObjectUnions.values()) {
                buildGraphQLTypeOutObjectUnion(codeRegistryBuilder, graphQLTypes, mergeGraphQLTypeOutObjectUnion);
            }

            GraphQLSchema schema = newSchema()
                    .query((GraphQLObjectType) graphQLTypes.get("query"))
                    .mutation((GraphQLObjectType) graphQLTypes.get("mutation"))
                    .subscription((GraphQLObjectType) graphQLTypes.get("subscription"))
                    .additionalTypes(new HashSet<GraphQLType>(graphQLTypes.values()))
                    .codeRegistry(codeRegistryBuilder.build())
                    .build();

            GraphQL graphQL = GraphQL.newGraphQL(schema)
                    .queryExecutionStrategy(new AsyncExecutionStrategy(dataFetcherExceptionHandler))
                    .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
                    .subscriptionExecutionStrategy(new SubscriptionExecutionStrategy(dataFetcherExceptionHandler))
                    .build();

            if (graphQLSchemaType.prepareCustomFields == null || graphQLSchemaType.prepareCustomFields.isEmpty()) {
                return new GraphQLExecutorImpl(schema, graphQL);
            } else {
                return new GraphQLExecutorPrepareImpl(component, schema, graphQL, buildGraphQLTypeOutObjects, buildGraphQLTypeOutObjectUnions, graphQLSchemaType);
            }
        } catch (Throwable throwable) {
            throw new GraphQLExecutorException(throwable);
        }
    }

    private void mergeGraphQLType(
            List<RGraphQLTypeEnum> buildGraphQLTypeEnums,
            Map<String, MergeGraphQLTypeOutObject> buildGraphQLTypeOutObjects,
            Map<String, MergeGraphQLTypeOutObjectInterface> buildGraphQLTypeOutObjectUnions,
            Map<String, Set<RGraphQLInputObjectTypeField>> buildGraphQLTypeInObjects,

            RGraphQLType rGraphQLType) throws GraphQLExecutorException {
        if (rGraphQLType instanceof RGraphQLTypeEnum) {
            buildGraphQLTypeEnums.add((RGraphQLTypeEnum) rGraphQLType);
        } else if (rGraphQLType instanceof RGraphQLTypeOutObject) {
            RGraphQLTypeOutObject rGraphQLObjectType = (RGraphQLTypeOutObject) rGraphQLType;

            String rTypeGraphQLName = rGraphQLType.getName();
            Set<RGraphQLObjectTypeField> rTypeGraphQLFields = new HashSet<RGraphQLObjectTypeField>(rGraphQLObjectType.getFields());

            MergeGraphQLTypeOutObject mergeGraphQLTypeOutObject = buildGraphQLTypeOutObjects.get(rTypeGraphQLName);
            if (mergeGraphQLTypeOutObject == null) {
                mergeGraphQLTypeOutObject = new MergeGraphQLTypeOutObject(rTypeGraphQLName, rGraphQLType.getDescription());
                buildGraphQLTypeOutObjects.put(rTypeGraphQLName, mergeGraphQLTypeOutObject);
            }

            //Мержим union типы
            for (String graphQLTypeUnionName : rGraphQLObjectType.getInterfaceGraphQLTypeNames()) {
                MergeGraphQLTypeOutObjectInterface mergeGraphQLTypeOutObjectUnion = buildGraphQLTypeOutObjectUnions.get(graphQLTypeUnionName);
                if (mergeGraphQLTypeOutObjectUnion == null) {
                    mergeGraphQLTypeOutObjectUnion = new MergeGraphQLTypeOutObjectInterface(graphQLTypeUnionName, rGraphQLType.getDescription());
                    buildGraphQLTypeOutObjectUnions.put(graphQLTypeUnionName, mergeGraphQLTypeOutObjectUnion);
                }
                mergeGraphQLTypeOutObjectUnion.mergePossible(rGraphQLObjectType.getClassName(), rTypeGraphQLName);
            }

            //Мержим
            mergeGraphQLTypeOutObject.mergeFields(rTypeGraphQLFields);
            mergeGraphQLTypeOutObject.mergeInterfaces(rGraphQLObjectType.getInterfaceGraphQLTypeNames());

        } else if (rGraphQLType instanceof RGraphQLTypeOutObjectInterface) {
            RGraphQLTypeOutObjectInterface rGraphQLTypeOutObjectUnion = (RGraphQLTypeOutObjectInterface) rGraphQLType;

            String rTypeGraphQLName = rGraphQLTypeOutObjectUnion.getName();

            MergeGraphQLTypeOutObjectInterface mergeGraphQLTypeOutObjectUnion = buildGraphQLTypeOutObjectUnions.get(rTypeGraphQLName);
            if (mergeGraphQLTypeOutObjectUnion == null) {
                mergeGraphQLTypeOutObjectUnion = new MergeGraphQLTypeOutObjectInterface(rTypeGraphQLName, rGraphQLType.getDescription());
                buildGraphQLTypeOutObjectUnions.put(rTypeGraphQLName, mergeGraphQLTypeOutObjectUnion);
            }

            //Добавляем общие поля
            mergeGraphQLTypeOutObjectUnion.mergeFields(rGraphQLTypeOutObjectUnion.getFields());

        } else if (rGraphQLType instanceof RGraphQLTypeInObject) {
            RGraphQLTypeInObject rGraphQLInputObjectType = (RGraphQLTypeInObject) rGraphQLType;

            String rTypeGraphQLName = rGraphQLInputObjectType.getName();
            Set<RGraphQLInputObjectTypeField> rTypeGraphQLFields = new HashSet<>(rGraphQLInputObjectType.getFields());

            if (buildGraphQLTypeInObjects.containsKey(rTypeGraphQLName)) {
                throw new GraphQLExecutorException("Not unique name: " + rTypeGraphQLName);
            }

            buildGraphQLTypeInObjects.put(rTypeGraphQLName, rTypeGraphQLFields);
        } else {
            throw new GraphQLExecutorException("Not support type: " + rGraphQLType);
        }
    }

    private GraphQLObjectType buildGraphQLTypeOutObject(GraphQLCodeRegistry.Builder codeRegistryBuilder, Map<String, GraphQLType> graphQLTypes, MergeGraphQLTypeOutObject graphQLTypeOutObject, Map<String, MergeGraphQLTypeOutObjectInterface> buildGraphQLTypeOutObjectUnions) throws GraphQLExecutorException {
        GraphQLObjectType.Builder graphQLObjectTypeBuilder = GraphQLObjectType.newObject();
        graphQLObjectTypeBuilder.name(graphQLTypeOutObject.name);

        if (graphQLTypeOutObject.description != null) {
            graphQLObjectTypeBuilder.description(graphQLTypeOutObject.description);
        }

        for (RGraphQLObjectTypeField typeGraphQLField : graphQLTypeOutObject.getFields()) {
            GraphQLFieldDefinition graphQLFieldDefinition = buildGraphQLFieldDefinition(codeRegistryBuilder, graphQLTypes, graphQLTypeOutObject.name, typeGraphQLField);
            graphQLObjectTypeBuilder.field(graphQLFieldDefinition);
        }

        for (String interfaceGraphQLTypeName : graphQLTypeOutObject.getInterfaceGraphQLTypeNames()) {
            graphQLObjectTypeBuilder.withInterface(new GraphQLTypeReference(interfaceGraphQLTypeName));

            //TODO необходимо отрефакторить - добавляем расширяющие полю от интерфейса
            //Необходимо добавлять только новые поля иначе перезаписывается новым полем с другим компонентом
            //в итоге выполнение уйдет не в ту подсистему
            MergeGraphQLTypeOutObjectInterface mergeGraphQLTypeOutObjectInterface = buildGraphQLTypeOutObjectUnions.get(interfaceGraphQLTypeName);
            for (RGraphQLObjectTypeField typeGraphQLField : mergeGraphQLTypeOutObjectInterface.getFields()) {
                //TODO необходимо отрефакторить! Логика совершено не очевидна
                if (!graphQLObjectTypeBuilder.hasField(typeGraphQLField.externalName)) {
                    GraphQLFieldDefinition graphQLFieldDefinition = buildGraphQLFieldDefinition(codeRegistryBuilder, graphQLTypes, graphQLTypeOutObject.name, typeGraphQLField);
                    graphQLObjectTypeBuilder.field(graphQLFieldDefinition);
                }
            }
        }

        GraphQLObjectType graphQLObjectType = graphQLObjectTypeBuilder.build();

        //Регистрируем этот тип
        graphQLTypes.put(graphQLTypeOutObject.name, graphQLObjectType);

        return graphQLObjectType;
    }

    private GraphQLInterfaceType buildGraphQLTypeOutObjectUnion(GraphQLCodeRegistry.Builder codeRegistryBuilder, Map<String, GraphQLType> graphQLTypes, MergeGraphQLTypeOutObjectInterface mergeGraphQLTypeOutObjectUnion) {

        GraphQLInterfaceType.Builder builder = GraphQLInterfaceType.newInterface().name(mergeGraphQLTypeOutObjectUnion.name);

        if (mergeGraphQLTypeOutObjectUnion.description != null) {
            builder.description(mergeGraphQLTypeOutObjectUnion.description);
        }

        for (RGraphQLObjectTypeField typeGraphQLField : mergeGraphQLTypeOutObjectUnion.getFields()) {
            GraphQLFieldDefinition graphQLFieldDefinition = buildGraphQLFieldDefinition(codeRegistryBuilder, graphQLTypes, mergeGraphQLTypeOutObjectUnion.name, typeGraphQLField);
            builder.field(graphQLFieldDefinition);
        }

        GraphQLInterfaceType graphQLInterfaceType = builder.build();

        //Регистрируем этот тип
        graphQLTypes.put(mergeGraphQLTypeOutObjectUnion.name, graphQLInterfaceType);

        //Добавляем резолвер
        codeRegistryBuilder.typeResolver(graphQLInterfaceType, new TypeResolver() {
            @Override
            public GraphQLObjectType getType(TypeResolutionEnvironment env) {
                String graphQLTypeName = mergeGraphQLTypeOutObjectUnion.getGraphQLTypeName(env.getObject().getClass().getName());
                if (graphQLTypeName == null) {
                    return null;
                } else {
                    return (GraphQLObjectType) graphQLTypes.get(graphQLTypeName);
                }
            }
        });

        return graphQLInterfaceType;
    }

    private GraphQLFieldDefinition buildGraphQLFieldDefinition(GraphQLCodeRegistry.Builder codeRegistryBuilder, Map<String, GraphQLType> graphQLTypes, String graphQLTypeName, RGraphQLObjectTypeField typeGraphQLField) {
        GraphQLFieldDefinition.Builder graphQLFieldDefinitionBuilder = GraphQLFieldDefinition.newFieldDefinition();

        graphQLFieldDefinitionBuilder.type(getGraphQLOutputType(graphQLTypes, typeGraphQLField.type))
                .name(typeGraphQLField.externalName);

        if (typeGraphQLField.description != null) {
            graphQLFieldDefinitionBuilder.description(typeGraphQLField.description);
        }

        if (typeGraphQLField.deprecated != null) {
            graphQLFieldDefinitionBuilder.deprecate(typeGraphQLField.deprecated);
        }

        if (typeGraphQLField.isField) {
            //Это обычное поле
            codeRegistryBuilder.dataFetcher(
                    FieldCoordinates.coordinates(graphQLTypeName, typeGraphQLField.externalName),
                    new ExtPropertyDataFetcher(typeGraphQLField.name)
            );
        } else {
            //Это у нас метод
            if (typeGraphQLField.arguments != null) {
                for (RGraphQLObjectTypeMethodArgument argument : typeGraphQLField.arguments) {
                    GraphQLArgument.Builder argumentBuilder = GraphQLArgument.newArgument();
                    argumentBuilder.name(argument.name);

                    if (argument.isNotNull) {
                        argumentBuilder.type(new GraphQLNonNull(getGraphQLInputType(graphQLTypes, argument.type)));
                    } else {
                        argumentBuilder.type(getGraphQLInputType(graphQLTypes, argument.type));
                    }

                    if (!Utils.isNullOrEmpty(argument.description)) {
                        argumentBuilder.description(argument.description);
                    }

                    graphQLFieldDefinitionBuilder.argument(argumentBuilder.build());
                }
            }

            ComponentDataFetcher componentDataFetcher;
            if (customRemoteDataFetcher != null) {
                try {
                    componentDataFetcher = (ComponentDataFetcher) customRemoteDataFetcher.newInstance(component.getRemotes(), sdkGraphQLItemExecutor, subscribeEngine, graphQLTypeName, typeGraphQLField);
                } catch (ReflectiveOperationException e) {
                    throw new GraphQLExecutorException("Exception build ComponentDataFetcher", e);
                }
            } else {
                componentDataFetcher = new ComponentDataFetcher(component.getRemotes(), sdkGraphQLItemExecutor, subscribeEngine, graphQLTypeName, typeGraphQLField);
            }
            codeRegistryBuilder.dataFetcher(
                    FieldCoordinates.coordinates(graphQLTypeName, typeGraphQLField.externalName),
                    componentDataFetcher
            );
        }

        return graphQLFieldDefinitionBuilder.build();
    }

    private GraphQLEnumType buildGraphQLTypeEnum(Map<String, GraphQLType> graphQLTypes, RGraphQLTypeEnum rGraphQLEnumType) {
        GraphQLEnumType.Builder graphQLObjectTypeEnumBuilder = GraphQLEnumType.newEnum();
        graphQLObjectTypeEnumBuilder.name(rGraphQLEnumType.getName());
        for (String enumValue : rGraphQLEnumType.getEnumValues()) {
            graphQLObjectTypeEnumBuilder.value(enumValue);
        }

        if (rGraphQLEnumType.getDescription() != null) {
            graphQLObjectTypeEnumBuilder.description(rGraphQLEnumType.getDescription());
        }

        GraphQLEnumType graphQLObjectTypeEnum = graphQLObjectTypeEnumBuilder.build();

        //Регистрируем этот тип
        graphQLTypes.put(rGraphQLEnumType.getName(), graphQLObjectTypeEnum);

        return graphQLObjectTypeEnum;
    }

    private GraphQLInputObjectType buildGraphQLTypeInObject(Map<String, GraphQLType> graphQLTypes, String graphQLTypeName, Set<RGraphQLInputObjectTypeField> fields) throws GraphQLExecutorException {
        GraphQLInputObjectType.Builder gBuilder = GraphQLInputObjectType.newInputObject();
        gBuilder.name(graphQLTypeName);

        for (RGraphQLInputObjectTypeField field : fields) {
            GraphQLInputObjectField.Builder fieldBuilder = GraphQLInputObjectField.newInputObjectField();
            fieldBuilder.name(field.externalName);

            if (field.isNotNull) {
                fieldBuilder.type(new GraphQLNonNull(getGraphQLInputType(graphQLTypes, field.type)));
            } else {
                fieldBuilder.type(getGraphQLInputType(graphQLTypes, field.type));
            }

            gBuilder.field(fieldBuilder.build());
        }

        GraphQLInputObjectType graphQLInputObjectType = gBuilder.build();

        //Регистрируем этот тип
        graphQLTypes.put(graphQLTypeName, graphQLInputObjectType);

        return graphQLInputObjectType;
    }

    private GraphQLOutputType getGraphQLOutputType(Map<String, GraphQLType> graphQLTypes, String type) throws GraphQLExecutorException {
        String[] compositeTypes = type.split(":");
        if (compositeTypes.length == 1) {//Это простой объект
            GraphQLType graphQLType = getType(graphQLTypes, type);
            if (graphQLType instanceof GraphQLOutputType) {
                return (GraphQLOutputType) graphQLType;
            } else {
                throw new GraphQLExecutorException("GraphQLType: " + type + " is not GraphQLOutputType");
            }
        } else if ("collection".equals(compositeTypes[0])) {
            return new GraphQLList(getType(graphQLTypes, compositeTypes[1]));
        } else {
            throw new GraphQLExecutorException("not support");
        }
    }

    private GraphQLInputType getGraphQLInputType(Map<String, GraphQLType> graphQLTypes, String type) throws GraphQLExecutorException {
        String[] compositeTypes = type.split(":");
        if (compositeTypes.length == 1) {//Это простой объект
            GraphQLType graphQLType = getType(graphQLTypes, type);
            if (graphQLType instanceof GraphQLOutputType) {
                return (GraphQLInputType) graphQLType;
            } else if (graphQLType instanceof GraphQLInputObjectType) {
                return (GraphQLInputType) graphQLType;
            } else {
                throw new GraphQLExecutorException("GraphQLType: " + type + " is not GraphQLInputType");
            }
        } else if ("collection".equals(compositeTypes[0])) {
            return new GraphQLList(getGraphQLInputType(graphQLTypes, compositeTypes[1]));
        } else {
            throw new GraphQLExecutorException("not support");
        }
    }

    private GraphQLType getType(Map<String, GraphQLType> graphQLTypes, String type) {
        GraphQLType graphQLType = graphQLTypes.get(type);
        if (graphQLType != null) {
            return graphQLType;
        } else {
            return new GraphQLTypeReference(type);
        }
    }
}
