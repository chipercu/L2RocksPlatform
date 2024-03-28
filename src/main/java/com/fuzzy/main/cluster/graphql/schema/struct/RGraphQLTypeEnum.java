package com.fuzzy.main.cluster.graphql.schema.struct;

import java.util.Collections;
import java.util.Set;

/**
 * Created by kris on 20.07.17.
 */
public class RGraphQLTypeEnum extends RGraphQLType {

    private final Set<String> enumValues;

    public RGraphQLTypeEnum(String name, String description, Set<String> enumValues) {
        super(name, description);
        this.enumValues = Collections.unmodifiableSet(enumValues);;
    }

    public Set<String> getEnumValues() {
        return enumValues;
    }

}
