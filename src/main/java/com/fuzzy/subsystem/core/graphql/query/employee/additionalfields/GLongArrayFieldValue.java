package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.util.ArrayList;

@GraphQLTypeOutObject("long_array_field_value")
public class GLongArrayFieldValue extends GFieldValue<ArrayList<Long>> {

    public GLongArrayFieldValue(AdditionalFieldReadable field, ArrayList<Long> value, boolean isSynchronized) {
        super(field, value, isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public ArrayList<Long> getValue() {
        return super.getValue();
    }
}