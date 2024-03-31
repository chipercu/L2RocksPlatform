package com.fuzzy.subsystem.core.graphql.query.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.config.*;
import com.fuzzy.subsystem.core.graphql.query.GCoreObjectConfigQuery;
import com.fuzzy.subsystem.core.remote.mail.RControllerMailConfigGetterWrapper;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;

@GraphQLTypeOutObject("app_config_core")
public class GQueryAppConfigCore {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Формат полного имени")
    public static GraphQLQuery<RemoteObject, DisplayNameFormat> getDisplayNameFormat() {
        GraphQLQuery<RemoteObject, DisplayNameFormat> query =
                new GCoreObjectConfigQuery<>(CoreConfigDescription.DISPLAY_NAME_FORMAT);
        return new GAccessQuery<>(query, CorePrivilege.GENERAL_SETTINGS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Язык сервера")
    public static GraphQLQuery<RemoteObject, Language> getServerLanguage() {
        return new GCoreObjectConfigQuery<>(CoreConfigDescription.SERVER_LANGUAGE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("День начала недели")
    public static GraphQLQuery<RemoteObject, FirstDayOfWeek> getFirstDayOfWeek() {
        return new GCoreObjectConfigQuery<>(CoreConfigDescription.FIRST_DAY_OF_WEEK);
    }

    @GraphQLField(value = "is_mail_configured")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Флаг указывающий на настроенный почтовый сервер")
    public static GraphQLQuery<RemoteObject, Boolean> isMailConfigured() {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<>() {
            private RControllerMailConfigGetterWrapper rControllerMailConfigGetterWrapper;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerMailConfigGetterWrapper
                        = resources.getQueryRemoteController(CoreSubsystem.class, RControllerMailConfigGetterWrapper.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rControllerMailConfigGetterWrapper.isMailConfigured();
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }


    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Тип авторизации")
    public static LogonType getLogonType(CoreSubsystem coreSubsystem) {
        return coreSubsystem.getConfig().getLogonType();
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Глубина дерева сотрудников")
    public static Integer getEmployeeTreeDepth(CoreSubsystem coreSubsystem) {
        return coreSubsystem.getConfig().getEmployeeTreeDepth();
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Режим debug")
    public static Boolean isDebugMode(CoreSubsystem coreSubsystem) {
        return coreSubsystem.getConfig().isDebugMode();
    }
}
