package com.fuzzy.platform.sdk.graphql.customfield.graphqlquery;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.exception.GraphQLExecutorDataFetcherException;
import com.fuzzy.cluster.graphql.preparecustomfield.PrepareCustomField;
import com.fuzzy.cluster.graphql.struct.ContextRequest;
import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.cluster.struct.Component;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQueryResourceProvider;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GraphQLQueryCustomField implements PrepareCustomField<com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery> {

    private final ConcurrentMap<GRequest, ConcurrentMap<String, com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery>> graphQLRequests;

    public GraphQLQueryCustomField() {
        graphQLRequests = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isSupport(Class clazz) {
        return (com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery.class.isAssignableFrom(clazz));
    }

    @Override
    public Type getEndType(Type genericType) {
        return ((ParameterizedType) genericType).getActualTypeArguments()[1];
    }

    @Override
    public Serializable requestPrepare(Component component, String keyField, com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery value, ContextRequest context) {
        ConcurrentMap<String, com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery> requestQueries = graphQLRequests.computeIfAbsent(
                context.getRequest(),
                s -> new ConcurrentHashMap<>()
        );

        if (requestQueries.putIfAbsent(keyField, value) != null) {
            throw new RuntimeException("Ошибка в логике работы - дублирующие идентификаторы: " + keyField);
        }

        try (GraphQLQueryResourceProvider resourceProvider = new GraphQLQueryResourceProvider((com.fuzzy.platform.sdk.component.Component) component)) {
            value.prepare(resourceProvider);
            return resourceProvider.getResources();
        }
    }


    @Override
    public Serializable execute(String keyFieldRequest, RemoteObject source, ContextRequest context) throws GraphQLExecutorDataFetcherException {
        ConcurrentMap<String, com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery> requestQueries = graphQLRequests.get(context.getRequest());
        GraphQLQuery graphQLQuery = requestQueries.get(keyFieldRequest);
        try {
            ContextTransactionRequest contextTransactionRequest = (ContextTransactionRequest) context;
            return graphQLQuery.execute(
                    source,
                    contextTransactionRequest
            );
        } catch (PlatformException e) {
            throw new GraphQLExecutorDataFetcherException(e);
        }
    }

    @Override
    public void requestCompleted(ContextRequest context) {
        graphQLRequests.remove(context.getRequest());
    }
}
