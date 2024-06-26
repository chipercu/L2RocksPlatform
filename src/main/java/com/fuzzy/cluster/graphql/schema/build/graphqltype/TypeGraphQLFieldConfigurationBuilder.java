package com.fuzzy.cluster.graphql.schema.build.graphqltype;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface TypeGraphQLFieldConfigurationBuilder<T extends RemoteObject> {

    T build(Class source, Method method);

    T build(Class source, Field field);
}
