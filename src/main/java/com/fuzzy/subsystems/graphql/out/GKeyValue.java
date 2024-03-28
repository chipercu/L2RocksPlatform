package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;

@GraphQLTypeOutObject("out_key_value")
public class GKeyValue implements RemoteObject {

    private String key;
    private String value;

    public GKeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Ключ")
    public String getKey() {
        return key;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Значение")
    public String getValue() {
        return value;
    }
}