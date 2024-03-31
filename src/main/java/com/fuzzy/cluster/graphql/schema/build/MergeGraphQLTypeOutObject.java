package com.fuzzy.cluster.graphql.schema.build;

import com.fuzzy.cluster.graphql.schema.build.MergeGraphQLType;
import com.fuzzy.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;

import java.util.*;

public class MergeGraphQLTypeOutObject extends MergeGraphQLType {

    private Map<String, RGraphQLObjectTypeField> fieldsByExternalName;
    private Set<String> interfaceGraphQLTypeNames;

    public MergeGraphQLTypeOutObject(String name, String description) {
        super(name, description);
        this.fieldsByExternalName = new HashMap<>();
        this.interfaceGraphQLTypeNames = new HashSet<>();
    }

    public void mergeFields(Set<RGraphQLObjectTypeField> rTypeGraphQLFields) {
        for (RGraphQLObjectTypeField field: rTypeGraphQLFields) {
            fieldsByExternalName.put(field.externalName, field);
        }
    }

    public void mergeInterfaces(Set<String> interfaceGraphQLTypeNames) {
        for (String interfaceGraphQLTypeName : interfaceGraphQLTypeNames) {
            this.interfaceGraphQLTypeNames.add(interfaceGraphQLTypeName);
        }
    }

    public Collection<RGraphQLObjectTypeField> getFields() {
        return fieldsByExternalName.values();
    }

    public RGraphQLObjectTypeField getFieldByExternalName(String externalName) {
        return fieldsByExternalName.get(externalName);
    }

    public Set<String> getInterfaceGraphQLTypeNames() {
        return Collections.unmodifiableSet(interfaceGraphQLTypeNames);
    }
}
