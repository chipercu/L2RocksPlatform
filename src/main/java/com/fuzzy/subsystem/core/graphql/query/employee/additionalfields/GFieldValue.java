package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.graphql.query.field.GAdditionalField;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

public abstract class GFieldValue<T> implements GFieldValueInterface {

    private final GAdditionalField field;
    private final T value;
    private final boolean isSynchronized;

    public GFieldValue(AdditionalFieldReadable field, T value, boolean isSynchronized) {
        this.field = new GAdditionalField(field);
        this.value = value;
        this.isSynchronized = isSynchronized;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Поле")
    public GAdditionalField getField() {
        return field;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    public T getValue() {
        return value;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение поля синхронизируется?")
    public boolean isSynchronized() {
        return isSynchronized;
    }
}
