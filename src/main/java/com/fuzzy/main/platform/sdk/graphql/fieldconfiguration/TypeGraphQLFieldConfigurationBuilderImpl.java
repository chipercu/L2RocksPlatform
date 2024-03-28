package com.fuzzy.main.platform.sdk.graphql.fieldconfiguration;

import com.fuzzy.main.cluster.graphql.schema.build.graphqltype.TypeGraphQLFieldConfigurationBuilder;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.fieldconfiguration.struct.FieldConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TypeGraphQLFieldConfigurationBuilderImpl implements TypeGraphQLFieldConfigurationBuilder<FieldConfiguration> {

    @Override
    public FieldConfiguration build(Class source, Method method) {
        boolean requiredAuthControl = method.getReturnType() != Class.class;

        GraphQLAuthControl authControl = method.getDeclaredAnnotation(GraphQLAuthControl.class);
        if (authControl == null) {
            if (requiredAuthControl) {
                throw new RuntimeException("Not found @GraphQLAuthControl in class: " + source + ", method: " + method.getName());
            }
            return new FieldConfiguration(UnauthorizedContext.class);
        } else if (requiredAuthControl) {
            return new FieldConfiguration(authControl.value());
        } else {
            throw new RuntimeException("Found @GraphQLAuthControl in class: " + source + ", method: " + method.getName());
        }
    }

    @Override
    public FieldConfiguration build(Class source, Field field) {
        GraphQLAuthControl authControl = field.getDeclaredAnnotation(GraphQLAuthControl.class);
        if (authControl == null) {
            throw new RuntimeException("Not found @GraphQLAuthControl in class: " + source + ", field: " + field.getName());
        }

        return new FieldConfiguration(authControl.value());
    }
}
