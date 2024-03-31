package com.fuzzy.cluster.graphql.schema.scalartype;

import com.fuzzy.cluster.graphql.schema.GraphQLSchemaType;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.util.Collections;
import java.util.Set;

public class GraphQLTypeScalar {

    private final String name;
    private final GraphQLScalarType graphQLScalarType;
    private final Set<Class> associationClasses;

    public GraphQLTypeScalar(GraphQLScalarType graphQLScalarType, Class associationClass) {
        this(
                graphQLScalarType,
                Collections.singleton(associationClass)
        );
    }

    public GraphQLTypeScalar(GraphQLScalarType graphQLScalarType, Set<Class> associationClasses) {
        this(
                GraphQLSchemaType.convertToGraphQLName(graphQLScalarType.getName()),
                graphQLScalarType,
                associationClasses
        );
    }

    public GraphQLTypeScalar(String customName, GraphQLScalarType graphQLScalarType, Set<Class> associationClasses) {
        this.name = customName;

        this.graphQLScalarType = graphQLScalarType;

        this.associationClasses = Set.copyOf(associationClasses);
    }

    public GraphQLTypeScalar(String name, String description, Class associationClass, Coercing coercing) {
        this(
                name,
                description,
                Collections.singleton(associationClass),
                coercing
        );
    }

    public GraphQLTypeScalar(String name, String description, Set<Class> associationClasses, Coercing coercing) {
        this.name = GraphQLSchemaType.convertToGraphQLName(name);

        this.graphQLScalarType = GraphQLScalarType.newScalar()
                .name(name)
                .description(description)
                .coercing(coercing)
                .build();

        this.associationClasses = Set.copyOf(associationClasses);
    }

    public String getName() {
        return name;
    }

    public GraphQLScalarType getGraphQLScalarType() {
        return graphQLScalarType;
    }

    public Set<Class> getAssociationClasses() {
        return associationClasses;
    }
}
