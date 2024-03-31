package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("long_field_value")
public class GLongFieldValue extends GFieldValue<Long> {

    public GLongFieldValue(AdditionalFieldReadable field, Long value, boolean isSynchronized) {
        super(field, value, isSynchronized);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    @Override
    public Long getValue() {
        return super.getValue();
    }
}
