package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.out.GOutDateTime;

import java.time.Instant;
import java.time.ZoneId;

@GraphQLTypeOutObject("date_time_field_value")
public class GDateTimeFieldValue extends GFieldValue<GOutDateTime> {

    public GDateTimeFieldValue(AdditionalFieldReadable field, Instant value, ZoneId zoneId, boolean isSynchronized) {
        super(field, value != null ? GOutDateTime.of(value, zoneId) : null, isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public GOutDateTime getValue() {
        return super.getValue();
    }
}