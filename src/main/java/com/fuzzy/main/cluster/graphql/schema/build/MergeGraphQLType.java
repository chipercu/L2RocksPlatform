package com.fuzzy.main.cluster.graphql.schema.build;

public abstract class MergeGraphQLType {

    public final String name;
    public final String description;

    public MergeGraphQLType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
