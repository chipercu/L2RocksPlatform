package com.fuzzy.main.cluster.graphql.schema.build;

import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MergeGraphQLTypeOutObjectInterface extends MergeGraphQLType {

    /**
     * key - class name
     * value - graphql name
     */
    private final Map<String, String> possibleType;

    private Map<String, RGraphQLObjectTypeField> fieldsByExternalName;

    public MergeGraphQLTypeOutObjectInterface(String name, String description) {
        super(name, description);
        this.possibleType = new HashMap<>();
        this.fieldsByExternalName = new HashMap<>();
    }

    public void mergePossible(String className, String graphQLTypeName) {
        possibleType.put(className, graphQLTypeName);
    }

    public void mergeFields(Set<RGraphQLObjectTypeField> rTypeGraphQLFields) {
        for (RGraphQLObjectTypeField field : rTypeGraphQLFields) {
            fieldsByExternalName.put(field.externalName, field);
        }
    }

    public Collection<RGraphQLObjectTypeField> getFields() {
        return fieldsByExternalName.values();
    }

    public String getGraphQLTypeName(String className) {
        return possibleType.get(className);
    }

    public RGraphQLObjectTypeField getFieldByExternalName(String externalName) {
        return fieldsByExternalName.get(externalName);
    }
}
