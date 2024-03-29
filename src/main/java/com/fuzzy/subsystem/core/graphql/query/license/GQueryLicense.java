package com.fuzzy.subsystem.core.graphql.query.license;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.subsystem.core.graphql.query.license.licenseconsumption.GQueryLicenseConsumption;
import com.fuzzy.subsystem.core.graphql.query.license.licensedemployee.GQueryLicensedEmployee;
import com.fuzzy.subsystem.core.graphql.query.license.licenselimits.GQueryLicenseLimits;

@GraphQLTypeOutObject("license_query")
public class GQueryLicense {

    @GraphQLField
    @GraphQLDescription("Текущее потребление лицензированных параметров")
    public static Class<GQueryLicenseConsumption> getLicenseConsumption() {
        return GQueryLicenseConsumption.class;
    }

    @GraphQLField
    @GraphQLDescription("Лимиты лицензированных параметров")
    public static Class<GQueryLicenseLimits> getLicenseLimits() {
        return GQueryLicenseLimits.class;
    }

    @GraphQLField
    @GraphQLDescription("Лицензированные сотрудники")
    public static Class<GQueryLicensedEmployee> getLicensedEmployees() {
        return GQueryLicensedEmployee.class;
    }

}
