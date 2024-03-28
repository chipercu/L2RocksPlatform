package com.fuzzy.main.cluster.graphql.executor.struct;

import graphql.language.SourceLocation;

public class GSourceLocation {

    private final SourceLocation sourceLocation;

    public GSourceLocation(SourceLocation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public int getLine() {
        return sourceLocation.getLine();
    }

    public int getColumn() {
        return sourceLocation.getColumn();
    }
}
