package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.config.AuthenticationConfig;
import com.fuzzy.subsystem.core.graphql.query.config.GComplexPasswordOutput;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.time.Duration;

@GraphQLTypeOutObject("integrated_authentication_content")
public class GIntegratedAuthenticationContent implements GAuthenticationContent {

    private final AuthenticationConfig authenticationConfig;

    public GIntegratedAuthenticationContent(AuthenticationConfig authenticationConfig) {
        this.authenticationConfig = authenticationConfig;
    }

    @GraphQLField
    @GraphQLAuthControl(AuthorizedContext.class)
    @GraphQLDescription("Сложность пароля")
    public GComplexPasswordOutput getComplexPassword() {
        return authenticationConfig.getComplexPassword() != null ?
                new GComplexPasswordOutput(authenticationConfig.getComplexPassword()) : null;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Время жизни пароля")
    public static GraphQLQuery<GIntegratedAuthenticationContent, Duration> getPasswordExpirationTime() {
        return new GraphQLQuery<GIntegratedAuthenticationContent, Duration>() {

            @Override
            public void prepare(ResourceProvider resources) {
            }

            @Override
            public Duration execute(GIntegratedAuthenticationContent source, ContextTransactionRequest context) {
                return source.authenticationConfig.getPasswordExpirationTime();
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Максимальное кол-во попыток авторизации")
    public static GraphQLQuery<GIntegratedAuthenticationContent, Integer> getMaxInvalidLogonCount() {
        return new GraphQLQuery<GIntegratedAuthenticationContent, Integer>() {

            @Override
            public void prepare(ResourceProvider resources) {
            }

            @Override
            public Integer execute(GIntegratedAuthenticationContent source, ContextTransactionRequest context) {
                return source.authenticationConfig.getMaxInvalidLogonCount();
            }
        };
    }
}