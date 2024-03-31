package com.fuzzy.cluster.graphql.preparecustomfield;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.exception.GraphQLExecutorDataFetcherException;
import com.fuzzy.cluster.graphql.struct.ContextRequest;
import com.fuzzy.cluster.struct.Component;

import java.io.Serializable;
import java.lang.reflect.Type;

public interface PrepareCustomField<T> {

    boolean isSupport(Class clazz);

    Type getEndType(Type genericType);

    Serializable requestPrepare(Component component, String keyField, T value, ContextRequest context);

    Serializable execute(String keyField, RemoteObject source, ContextRequest context) throws GraphQLExecutorDataFetcherException;

    void requestCompleted(ContextRequest context);
}