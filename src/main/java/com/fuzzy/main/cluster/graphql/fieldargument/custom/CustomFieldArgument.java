package com.fuzzy.main.cluster.graphql.fieldargument.custom;

import com.fuzzy.main.cluster.graphql.struct.ContextRequest;

import java.lang.reflect.Method;

/**
 * Created by user on 06.09.2017.
 */
public interface CustomFieldArgument<T> {

    boolean isSupport(Class classType);

    T getValue(Class classType, Method method, ContextRequest context);
}
