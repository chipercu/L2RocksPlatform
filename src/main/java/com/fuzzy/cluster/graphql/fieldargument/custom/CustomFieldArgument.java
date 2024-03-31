package com.fuzzy.cluster.graphql.fieldargument.custom;

import com.fuzzy.cluster.graphql.struct.ContextRequest;

import java.lang.reflect.Method;

/**
 * Created by user on 06.09.2017.
 */
public interface CustomFieldArgument<T> {

    boolean isSupport(Class classType);

    T getValue(Class classType, Method method, ContextRequest context);
}
