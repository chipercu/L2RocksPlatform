package com.fuzzy.cluster.exception;

import com.fuzzy.cluster.exception.ClusterException;
import com.fuzzy.cluster.struct.Component;

public class ClusterDependencyException extends ClusterException {

    public ClusterDependencyException(Component referencingComponent, Component removingComponent) {
        super(String.format("Component %s referenced to removing %s.",
                referencingComponent.getClass().getName(),
                removingComponent.getClass().getName()));
    }
}
