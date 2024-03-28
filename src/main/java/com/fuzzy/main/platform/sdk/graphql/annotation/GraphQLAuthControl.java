package com.fuzzy.main.platform.sdk.graphql.annotation;

import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLAuthControl {

    Class<? extends UnauthorizedContext>[] value();

}
