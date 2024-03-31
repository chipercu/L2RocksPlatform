package com.fuzzy.cluster.graphql.schema.struct.in;

import com.fuzzy.cluster.graphql.schema.struct.RGraphQLType;
import com.fuzzy.cluster.graphql.schema.struct.in.RGraphQLInputObjectTypeField;

import java.util.Collections;
import java.util.Set;

/**
 * Created by kris on 20.07.17.
 */
public class RGraphQLTypeInObject extends RGraphQLType {

    private final Set<RGraphQLInputObjectTypeField> fields;

    public RGraphQLTypeInObject(String name, String description, Set<RGraphQLInputObjectTypeField> fields) {
        super(name, description);
        this.fields = Collections.unmodifiableSet(fields);
    }

    public Set<RGraphQLInputObjectTypeField> getFields() {
        return fields;
    }

}
