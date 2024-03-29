package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObjectInterface;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.graphql.query.field.GAdditionalField;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObjectInterface("field_value_interface")
public interface GFieldValueInterface extends RemoteObject {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Поле")
    GAdditionalField getField();

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение поля синхронизируется?")
    boolean isSynchronized();
}