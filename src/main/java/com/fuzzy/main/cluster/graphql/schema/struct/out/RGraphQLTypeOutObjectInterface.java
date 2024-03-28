package com.fuzzy.main.cluster.graphql.schema.struct.out;

import com.fuzzy.main.cluster.graphql.schema.struct.RGraphQLType;

import java.util.Collections;
import java.util.Set;

/**
 * Created by kris on 29.12.16.
 */
public class RGraphQLTypeOutObjectInterface extends RGraphQLType {

    private final Set<RGraphQLObjectTypeField> fields;

    public RGraphQLTypeOutObjectInterface(String name, String description, Set<RGraphQLObjectTypeField> fields) {
        super(name, description);
        this.fields = Collections.unmodifiableSet(fields);
    }

    public Set<RGraphQLObjectTypeField> getFields() {
        return fields;
    }

}
