package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.out.GOutDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("date_array_field_value")
public class GDateArrayFieldValue extends GFieldValue<ArrayList<GOutDate>> {

    public GDateArrayFieldValue(AdditionalFieldReadable field, List<LocalDate> value, boolean isSynchronized) {
        super(field,
                value != null ? value.stream().map(GOutDate::of).collect(Collectors.toCollection(ArrayList::new)) : null,
                isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public ArrayList<GOutDate> getValue() {
        return super.getValue();
    }
}