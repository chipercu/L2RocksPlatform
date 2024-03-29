package com.fuzzy.subsystem.core.graphql.query.license.licenseconsumption;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.license.licenseconsumption.LicenseConsumptionGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("license_consumption_query")
public class GQueryLicenseConsumption {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Текущий размер команды")
    public static GraphQLQuery<RemoteObject, Long> getUsersWithRole() {
        return new GraphQLQuery<>() {
            private LicenseConsumptionGetter licenseConsumptionGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                licenseConsumptionGetter = new LicenseConsumptionGetter(resources);
            }

            @Override
            public Long execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return licenseConsumptionGetter.getUsersWithRole(context);
            }
        };
    }
}
