package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.out.GOutDate;

import java.time.LocalDate;

@GraphQLTypeOutObject("date_field_value")
public class GDateFieldValue extends GFieldValue<GOutDate> {

    public GDateFieldValue(AdditionalFieldReadable field, LocalDate value, boolean isSynchronized) {
        super(field, value != null ? GOutDate.of(value) : null, isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public GOutDate getValue() {
        return super.getValue();
    }
}
