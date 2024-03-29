package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.graphql.query.employee.GDepartmentEmployeeElement;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("entity_field_value")
public class GEntityFieldValue extends GFieldValue<GDepartmentEmployeeElement> {

    private final Long longValue;

    public GEntityFieldValue(AdditionalFieldReadable field, Long longValue, GDepartmentEmployeeElement value, boolean isSynchronized) {
        super(field, value, isSynchronized);
        this.longValue = longValue;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public GDepartmentEmployeeElement getValue() {
        return super.getValue();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    public Long getLongValue() {
        return longValue;
    }
}
