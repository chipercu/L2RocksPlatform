package com.fuzzy.subsystem.core.graphql.query.license.licenselimits;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("License_limits_query")
public class GQueryLicenseLimits {
    @GraphQLField
    @GraphQLDescription("Текущее потребление лицензированных параметров")
    public static Class<GQueryParametersLimits> getParametersLimits() {
        return GQueryParametersLimits.class;
    }

    @GraphQLField
    @GraphQLDescription("Текущее потребление лицензированных параметров")
    public static Class<GQueryBusinessRolesLimits> getBusinessRolesLimits() {
        return GQueryBusinessRolesLimits.class;
    }

}
