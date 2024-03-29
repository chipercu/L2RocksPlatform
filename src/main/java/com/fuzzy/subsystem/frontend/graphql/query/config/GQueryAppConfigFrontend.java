package com.fuzzy.subsystem.frontend.graphql.query.config;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;

import java.nio.file.Files;
import java.nio.file.Path;

@GraphQLTypeOutObject("app_config_frontend")
public class GQueryAppConfigFrontend {

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Информация о сервисном режиме")
    public static ServiceModeInfo getServiceModeInfo(FrontendSubsystem frontendSubsystem) {
        boolean enabled = frontendSubsystem.getConfig().isServiceMode();
        String message = enabled ? frontendSubsystem.getConfig().getServiceModeMessage() : null;
        return new ServiceModeInfo(enabled, message);
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Доступность документации")
    public static GraphQLQuery<RemoteObject, Boolean> getDocsAvailability(FrontendSubsystem frontendSubsystem) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            @Override
            public void prepare(ResourceProvider resources) { }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) {
                Path docsPath = frontendSubsystem.getDocsDirPath();
                return Files.exists(docsPath) && Files.isDirectory(docsPath);
            }
        };
    }

}
