package com.fuzzy.cluster.graphql.schema.datafetcher;

import com.fuzzy.cluster.core.remote.RemoteTarget;
import com.fuzzy.cluster.core.remote.Remotes;
import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.fuzzy.cluster.exception.ClusterRemotePackerException;
import com.fuzzy.cluster.graphql.exception.GraphQLExecutorDataFetcherException;
import com.fuzzy.cluster.graphql.executor.component.GraphQLComponentExecutor;
import com.fuzzy.cluster.graphql.executor.subscription.GraphQLSubscribeEngineImpl;
import com.fuzzy.cluster.graphql.preparecustomfield.PrepareCustomFieldUtils;
import com.fuzzy.cluster.graphql.remote.graphql.executor.RControllerGraphQLExecutor;
import com.fuzzy.cluster.graphql.schema.datafetcher.utils.ExtResult;
import com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.fuzzy.cluster.graphql.struct.ContextRequest;
import com.fuzzy.cluster.graphql.struct.GSubscribeEvent;
import graphql.language.Argument;
import graphql.language.Field;
import graphql.language.VariableReference;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by kris on 11.01.17.
 */
public class ComponentDataFetcher implements DataFetcher {

    protected final static Logger log = LoggerFactory.getLogger(ComponentDataFetcher.class);

    protected final Remotes remotes;
    protected final GraphQLComponentExecutor sdkGraphQLItemExecutor;
    protected final GraphQLSubscribeEngineImpl subscribeEngine;

    protected final String graphQLTypeName;
    protected final RGraphQLObjectTypeField rTypeGraphQLField;

    public ComponentDataFetcher(Remotes remotes, GraphQLComponentExecutor sdkGraphQLItemExecutor, GraphQLSubscribeEngineImpl subscribeEngine, String graphQLTypeName, RGraphQLObjectTypeField rTypeGraphQLField) {
        this.remotes = remotes;
        this.sdkGraphQLItemExecutor = sdkGraphQLItemExecutor;
        this.subscribeEngine = subscribeEngine;

        this.graphQLTypeName = graphQLTypeName;
        this.rTypeGraphQLField = rTypeGraphQLField;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        try {
            return execute(environment);
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    protected Object execute(DataFetchingEnvironment environment) throws Throwable {
        ContextRequest context = environment.getContext();
        try {
            Object result;
            if (rTypeGraphQLField.componentId == null) {
                //У этого объекта нет родительской подсистемы - вызываем прямо тут

                if (rTypeGraphQLField.isPrepare) throw new RuntimeException("Not implemented");

                result = sdkGraphQLItemExecutor.execute(
                        environment.getSource(), graphQLTypeName, rTypeGraphQLField.name,
                        getArguments(rTypeGraphQLField, environment, context.getRequest().getQueryVariables()),
                        context
                );
            } else {
                //Этот объект принадлежит определенной подсистеме - необходимо вызывать метод удаленно именно не родительской подсистеме
                LocationRuntimeComponent runtimeComponentInfo = remotes.component.getTransport().getNetworkTransit().getManagerRuntimeComponent().get(rTypeGraphQLField.nodeRuntimeId, rTypeGraphQLField.componentId);
                if (runtimeComponentInfo == null) {
                    throw new ClusterRemotePackerException();
                }
                RemoteTarget target = new RemoteTarget(rTypeGraphQLField.nodeRuntimeId, rTypeGraphQLField.componentId, runtimeComponentInfo.component().uuid);

                RControllerGraphQLExecutor rControllerGraphQLExecutor = remotes.getFromCKey(target, RControllerGraphQLExecutor.class);

                RemoteObject source = null;
                if (environment.getSource() instanceof RemoteObject) {
                    source = environment.getSource();
                }

                if (rTypeGraphQLField.isPrepare) {
                    result = rControllerGraphQLExecutor.executePrepare(
                            PrepareCustomFieldUtils.getKeyField(environment),
                            source,
                            context
                    );
                } else {
                    result = rControllerGraphQLExecutor.execute(
                            source, graphQLTypeName, rTypeGraphQLField.name,
                            getArguments(rTypeGraphQLField, environment, context.getRequest().getQueryVariables()),
                            context
                    );
                }
            }

            result = ExtResult.get(result);

            if (result instanceof GSubscribeEvent.SubscribeValue) {//Подписка
                GSubscribeEvent.SubscribeValue resultSubscribeValue = (GSubscribeEvent.SubscribeValue) result;
                ObservableOnSubscribe observableOnSubscribe = new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter emitter) {
                        emitter.onNext(resultSubscribeValue.value);
                        subscribeEngine.subscribe(rTypeGraphQLField.nodeRuntimeId, rTypeGraphQLField.componentId, resultSubscribeValue.subscribeKey, emitter);
                    }
                };
                return Observable.create(observableOnSubscribe).toFlowable(BackpressureStrategy.LATEST);
            } else {
                return result;
            }
        } catch (Throwable t) {
            Throwable e;
            if (t instanceof InvocationTargetException) {
                e = ((InvocationTargetException) t).getTargetException();
            } else if (t instanceof ExecutionException) {
                e = t.getCause();
            } else if (t instanceof GraphQLExecutorDataFetcherException) {
                e = t.getCause();
            } else {
                e = t;
            }
            throw e;
        }
    }


    /**
     * Вытаскиваем из запроса пришедшие аргументы
     */
    protected static HashMap<String, Serializable> getArguments(RGraphQLObjectTypeField rTypeGraphQLField, DataFetchingEnvironment environment, HashMap<String, Serializable> externalVariables) {
        Field field = null;
        for (Field iField : environment.getMergedField().getFields()) {
            if (iField.getName().equals(rTypeGraphQLField.externalName)) {
                field = iField;
                break;
            }
        }
        if (field == null || field.getArguments().isEmpty()) return new HashMap<>();

        return filterArguments(field, environment.getArguments(), externalVariables.keySet());
    }

    /**
     * Фильтруем из запроса пришедшие аргументы
     */
    public static HashMap<String, Serializable> filterArguments(Field field, Map<String, Object> arguments, Set<String> externalNameVariables) {
        HashMap<String, Serializable> result = new HashMap<String, Serializable>();
        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            Argument argument = getArgument(field, entry.getKey());
            if (argument.getValue() instanceof VariableReference) {
                //Проверим хитрую ситуацию, если аргумент в методе был зарезервирован под переменную из variables
                //но этот variable не был передан, то считаем, что этот агрумент и не собирались отправлять
                VariableReference variableReference = (VariableReference) argument.getValue();
                if (!externalNameVariables.contains(variableReference.getName())) continue;
            }
            result.put(entry.getKey(), (Serializable) entry.getValue());
        }
        return result;
    }

    private static Argument getArgument(Field field, String name) {
        for (Argument argument : field.getArguments()) {
            if (argument.getName().equals(name)) return argument;
        }
        throw new RuntimeException();//Такого быть не должно в принципе
    }

}
