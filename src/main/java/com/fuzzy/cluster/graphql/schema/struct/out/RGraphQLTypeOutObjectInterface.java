package com.fuzzy.cluster.graphql.schema.struct.out;

import com.fuzzy.cluster.graphql.schema.struct.RGraphQLType;
import com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;

import java.util.Collections;
import java.util.Set;

/**
 * Created by kris on 29.12.16.
 */
public class RGraphQLTypeOutObjectInterface extends RGraphQLType {

    private final Set<com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField> fields;

    public RGraphQLTypeOutObjectInterface(String name, String description, Set<com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField> fields) {
        super(name, description);
        this.fields = Collections.unmodifiableSet(fields);
    }

    public Set<RGraphQLObjectTypeField> getFields() {
        return fields;
    }

}
