package com.fuzzy.subsystem.core.graphql.query.field;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.remote.fieldsgetter.SystemFieldDescription;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;

import java.util.ArrayList;

@GraphQLTypeOutObject("system_field")
public class GSystemField implements RemoteObject {

    private final SystemFieldDescription field;

    public GSystemField(SystemFieldDescription field) {
        this.field = field;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Ключ")
    public static GraphQLQuery<GSystemField, String> getKey() {
        return new GAccessQuery<>(gSystemField -> gSystemField.field.getKey(),
                CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Тип данных")
    public static GraphQLQuery<GSystemField, FieldDataType> getDataType() {
        return new GAccessQuery<>(gSystemField -> gSystemField.field.getDataType(),
                CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Интеграции")
    public static GraphQLQuery<GSystemField, ArrayList<String>> getIntegrations() {
        GraphQLQuery<GSystemField, ArrayList<String>> query = new GraphQLQuery<>() {

            private RCIntegrationsExecutor rcIntegrations;

            @Override
            public void prepare(ResourceProvider resources) {
                rcIntegrations = new RCIntegrationsExecutor(resources);
            }

            @Override
            public ArrayList<String> execute(GSystemField source, ContextTransactionRequest context) throws PlatformException {
                return rcIntegrations.getIntegrations(EmployeeReadable.class.getName(), source.field.getKey(), context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }
}
