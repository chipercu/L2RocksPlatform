package com.fuzzy.platform.sdk.graphql.datafetcher;

import com.fuzzy.cluster.core.remote.Remotes;
import com.fuzzy.cluster.graphql.executor.component.GraphQLComponentExecutor;
import com.fuzzy.cluster.graphql.executor.subscription.GraphQLSubscribeEngineImpl;
import com.fuzzy.cluster.graphql.schema.datafetcher.ComponentDataFetcher;
import com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.impl.ContextTransactionRequestImpl;
import com.fuzzy.platform.exception.runtime.PlatformRuntimeException;
import com.fuzzy.platform.sdk.context.ContextUtils;
import com.fuzzy.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.platform.sdk.graphql.fieldconfiguration.struct.FieldConfiguration;
import com.fuzzy.platform.utils.ExceptionUtils;
import graphql.schema.DataFetchingEnvironment;

/**
 * Created by kris on 11.01.17.
 */
public class PlatformDataFetcher extends ComponentDataFetcher {

    public PlatformDataFetcher(Remotes remotes, GraphQLComponentExecutor sdkGraphQLItemExecutor, GraphQLSubscribeEngineImpl subscribeEngine, String graphQLTypeName, RGraphQLObjectTypeField rTypeGraphQLField) {
        super(remotes, sdkGraphQLItemExecutor, subscribeEngine, graphQLTypeName, rTypeGraphQLField);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        ContextTransactionRequestImpl context = environment.getContext();

        UnauthorizedContext authContext = context.getSource().getAuthContext();

        //Проверяем, возможно поле требуется авторизации, тогда надо проверить, что права совпадают
        FieldConfiguration fieldConfiguration = (FieldConfiguration) rTypeGraphQLField.configuration;
        boolean isAccess = false;
        for (Class<? extends UnauthorizedContext> typeAuthContext : fieldConfiguration.typeAuthContexts) {
            if (typeAuthContext.isAssignableFrom(authContext.getClass())) {
                isAccess = true;
                break;
            }
        }

        if (!isAccess) {
            throw new PlatformRuntimeException(GeneralExceptionBuilder.buildInvalidCredentialsException(rTypeGraphQLField.type, rTypeGraphQLField.name));
        }

        try {
            return execute(environment);
        } catch (Throwable t) {
            throw ExceptionUtils.coercionRuntimeException(t);
        }
    }

    private String getExceptionDetails(ContextTransactionRequestImpl context) {
        return "Request " + ContextUtils.toTrace(context);
    }
}
