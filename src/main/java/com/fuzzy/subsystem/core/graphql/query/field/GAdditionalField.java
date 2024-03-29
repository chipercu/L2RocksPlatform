package com.fuzzy.subsystem.core.graphql.query.field;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.enums.ListSourceType;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.utils.StringUtils;

import java.util.ArrayList;

@GraphQLTypeOutObject("additional_field")
public class GAdditionalField extends GDomainObject<AdditionalFieldReadable> {

    public GAdditionalField(AdditionalFieldReadable source) {
        super(source);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Идентификатор")
    public long getId() {
        return super.getSource().getId();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Название")
    public static GraphQLQuery<GAdditionalField, String> getName() {
        return new GAccessQuery<>(gAdditionalField -> gAdditionalField.getSource().getName(),
                CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Тип данных")
    public static GraphQLQuery<GAdditionalField, FieldDataType> getDataType() {
        return new GAccessQuery<>(gAdditionalField -> gAdditionalField.getSource().getDataType(),
                CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Порядок расположения")
    public static GraphQLQuery<GAdditionalField, Integer> getOrder() {
        return new GAccessQuery<>(gAdditionalField -> gAdditionalField.getSource().getOrder(),
                CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Источник данных для списка")
    public static GraphQLQuery<GAdditionalField, ListSourceType> getListSource() {
        return new GAccessQuery<>(gAdditionalField -> {
            final String listSource = gAdditionalField.getSource().getListSource();
            if (StringUtils.isEmpty(listSource)) {
                return null;
            }
            return ListSourceType.get(listSource);
        }, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Интеграции")
    public static GraphQLQuery<GAdditionalField, ArrayList<String>> getIntegrations() {
        GraphQLQuery<GAdditionalField, ArrayList<String>> query = new GraphQLQuery<>() {

            private RCIntegrationsExecutor rcIntegrations;

            @Override
            public void prepare(ResourceProvider resources) {
                rcIntegrations = new RCIntegrationsExecutor(resources);
            }

            @Override
            public ArrayList<String> execute(GAdditionalField source, ContextTransactionRequest context) throws PlatformException {
                return rcIntegrations.getIntegrations(source.getSource().getObjectType(), source.getSource().getKey(), context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_FIELDS, AccessOperation.READ);
    }
}
