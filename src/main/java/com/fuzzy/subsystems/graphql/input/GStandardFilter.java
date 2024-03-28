package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

@GraphQLTypeInput("standard_filter")
public class GStandardFilter implements RemoteObject {

    private final GFilterOperation operation;
    private final HashSet<Long> nodes;
    private final HashSet<Long> items;

    public GStandardFilter(
            @GraphQLDescription("Действие")
            @NonNull @GraphQLName("operation") GFilterOperation operation,
            @GraphQLDescription("Идентификаторы групп")
            @GraphQLName("nodes") HashSet<Long> nodes,
            @GraphQLDescription("Идентификаторы элементов")
            @GraphQLName("items") HashSet<Long> items) {
        this.operation = operation;
        this.nodes = nodes;
        this.items = items;
    }

    public HashSet<Long> getNodes() {
        return nodes;
    }

    public HashSet<Long> getItems() {
        return items;
    }

    public GFilterOperation getOperation() {
        return operation;
    }
}