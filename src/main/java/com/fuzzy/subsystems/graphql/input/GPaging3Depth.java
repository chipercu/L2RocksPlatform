package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

@GraphQLTypeInput("paging_3_depth")
public class GPaging3Depth {

    private final ArrayList<GPagingNode> nodes;
    private final int defaultLimit;

    public GPaging3Depth(
            @GraphQLDescription("Параметры пейджинга для отдельных групп")
            @Nullable @GraphQLName("nodes") ArrayList<GPagingNode> nodes,
            @GraphQLDescription("Количество отображаемых элементов по-умолчанию")
            @NonNull @GraphQLName("default_limit") Integer defaultLimit) {
        this.nodes = nodes;
        this.defaultLimit = defaultLimit;
    }

    public @Nullable ArrayList<GPagingNode> getNodes() {
        return nodes;
    }

    public int getDefaultLimit() {
        return Math.max(defaultLimit, 0);
    }

    public int getLimit(long nodeId) {
        if (nodes != null) {
            return nodes.stream().filter(node -> nodeId == node.getId()).map(GPagingNode::getLimit).findAny().orElse(getDefaultLimit());
        }
        return getDefaultLimit();
    }

    public @Nullable GPagingNode getNode(long nodeId) {
        if (nodes != null) {
            return nodes.stream().filter(node -> nodeId == node.getId()).findAny().orElse(null);
        }
        return null;
    }
}