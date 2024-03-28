package com.fuzzy.main.cluster.graphql.utils;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class ReflectionUtils {

    public static Constructor getGConstructor(Class classGraphQLTypeInput) {
        Constructor constructor = null;
        for (Constructor iConstructor : classGraphQLTypeInput.getConstructors()) {
            Annotation[][] annotations = iConstructor.getParameterAnnotations();
            if (annotations.length == 0) {
                continue;
            }
            //Ищем в у первого параметра аннотацию GraphQL
            for (Annotation annotation : annotations[0]) {
                if (annotation.annotationType() == GraphQLName.class) {
                    if (constructor == null) {
                        constructor = iConstructor;
                        break;
                    } else {
                        throw new RuntimeException("Undefined behavior, multiple constructors with GraphQLName annotation: " + classGraphQLTypeInput.getName());
                    }
                }
            }

        }
        return constructor;
    }
}
