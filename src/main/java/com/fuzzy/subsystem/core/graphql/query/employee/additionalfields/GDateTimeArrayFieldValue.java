package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.out.GOutDateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("date_time_array_field_value")
public class GDateTimeArrayFieldValue extends GFieldValue<ArrayList<GOutDateTime>> {

    public GDateTimeArrayFieldValue(AdditionalFieldReadable field, List<Instant> value, ZoneId zoneId, boolean isSynchronized) {
        super(field, value != null ? value.stream().map(instant -> GOutDateTime.of(instant, zoneId))
                .collect(Collectors.toCollection(ArrayList::new)) : null,
                isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public ArrayList<GOutDateTime> getValue() {
        return super.getValue();
    }
}