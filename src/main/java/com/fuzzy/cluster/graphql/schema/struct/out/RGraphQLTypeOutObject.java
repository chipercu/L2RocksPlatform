package com.fuzzy.cluster.graphql.schema.struct.out;

import com.fuzzy.cluster.graphql.schema.struct.RGraphQLType;
import com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;

import java.util.Collections;
import java.util.Set;

/**
 * Created by kris on 20.07.17.
 */
public class RGraphQLTypeOutObject extends RGraphQLType {

    private final String className;

    private final Set<String> interfaceGraphQLTypeNames;
    private final Set<RGraphQLObjectTypeField> fields;

    public RGraphQLTypeOutObject(String name, String description, String className, Set<String> interfaceGraphQLTypeNames, Set<RGraphQLObjectTypeField> fields) {
        super(name, description);
        this.className = className;
        this.interfaceGraphQLTypeNames = Collections.unmodifiableSet(interfaceGraphQLTypeNames);
        this.fields = Collections.unmodifiableSet(fields);
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getInterfaceGraphQLTypeNames() {
        return interfaceGraphQLTypeNames;
    }

    public Set<RGraphQLObjectTypeField> getFields() {
        return fields;
    }

}
