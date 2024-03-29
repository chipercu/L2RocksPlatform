package com.fuzzy.subsystem.core.graphql.query.license.licenselimits;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import com.fuzzy.subsystem.core.remote.liscense.RCLicenseGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("parameters_limits_query")
public class GQueryParametersLimits {
    private static final String MONITORING_UUID = "com.infomaximum.subsystem.monitoring";
    private static final String AUTOMATION_UUID = "com.infomaximum.subsystem.automation";
    private static final String BI_DATA_UUID = "com.infomaximum.subsystem.bidata";
    private static final String CORE_UUID = "com.infomaximum.subsystem.core";
    private static final String AUTOMATION_WEBHOOK_UUID = "com.infomaximum.subsystem.automationwebhook";
    private static final String DASHBOARD_UUID = "com.infomaximum.subsystem.dashboard";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Место на диске")
    public static GraphQLQuery<RemoteObject, Long> getDiskSpace(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(BI_DATA_UUID, LicenseParameter.DISK_SPACE);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Период между выполнениями скрипта")
    public static GraphQLQuery<RemoteObject, Long> getMinScriptRunPeriod(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(AUTOMATION_UUID, LicenseParameter.MIN_SCRIPT_RUN_PERIOD);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Лимит отправки писем")
    public static GraphQLQuery<RemoteObject, Long> getMailSending(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(AUTOMATION_UUID, LicenseParameter.MAIL_SENDING);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Простой мониторинг")
    public static GraphQLQuery<RemoteObject, Long> getMonitoringSimple(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(MONITORING_UUID, LicenseParameter.MONITORING_SIMPLE);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Расширенный мониторинг")
    public static GraphQLQuery<RemoteObject, Long> getMonitoringExtended(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(MONITORING_UUID, LicenseParameter.MONITORING_EXTENDED);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Размер команды")
    public static GraphQLQuery<RemoteObject, Long> getUsersWithRole(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(CORE_UUID, LicenseParameter.USERS_WITH_ROLE);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Суммарное время выполнения скрипта")
    public static GraphQLQuery<RemoteObject, Long> getScriptExecutionTime(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(AUTOMATION_UUID, LicenseParameter.SCRIPT_EXECUTION_TIME);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Уникальные посетители (MTU)")
    public static GraphQLQuery<RemoteObject, Long> getMTU(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(AUTOMATION_WEBHOOK_UUID, LicenseParameter.MTU);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Максимальная длительность выполнения SQL-запроса")
    public static GraphQLQuery<RemoteObject, Long> getClickhouseSqlQueryMaxDuration(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(DASHBOARD_UUID, LicenseParameter.CLICKHOUSE_SQL_QUERY_MAX_DURATION);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Максимальное кол-во памяти на один запрос в ClickHouse")
    public static GraphQLQuery<RemoteObject, Long> getClickhouseMaxMemoryUsage(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getModuleParameterLimit(BI_DATA_UUID, LicenseParameter.CLICKHOUSE_MAX_MEMORY_USAGE);
            }
        };
    }
}
