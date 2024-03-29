package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.util.ArrayList;

@GraphQLTypeOutObject("string_array_field_value")
public class GStringArrayFieldValue extends GFieldValue<ArrayList<String>> {

    public GStringArrayFieldValue(AdditionalFieldReadable field, ArrayList<String> value, boolean isSynchronized) {
        super(field, value, isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public ArrayList<String> getValue() {
        return super.getValue();
    }
}