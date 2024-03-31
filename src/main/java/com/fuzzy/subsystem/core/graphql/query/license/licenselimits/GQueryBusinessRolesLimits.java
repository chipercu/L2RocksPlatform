package com.fuzzy.subsystem.core.graphql.query.license.licenselimits;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.remote.liscense.RCLicenseGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("business_roles_limits_query")
public class GQueryBusinessRolesLimits {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Администраторы")
    public static GraphQLQuery<RemoteObject, Long> getAdmins(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getBusinessRoleLimit(BusinessRoleLimit.ADMIN);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Аналитики")
    public static GraphQLQuery<RemoteObject, Long> getAnalysts(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getBusinessRoleLimit(BusinessRoleLimit.ANALYST);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Бизнесс-пользователь")
    public static GraphQLQuery<RemoteObject, Long> getBusinessUsers(CoreSubsystem component) {
        return new GraphQLQuery<>() {
            private RCLicenseGetter rcLicenseGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcLicenseGetter.getBusinessRoleLimit(BusinessRoleLimit.BUSINESS_USER);
            }
        };
    }
}
