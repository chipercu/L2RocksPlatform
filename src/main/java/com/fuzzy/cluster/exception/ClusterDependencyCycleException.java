package com.fuzzy.cluster.exception;

import com.fuzzy.cluster.exception.ClusterException;

import java.util.List;

public class ClusterDependencyCycleException extends ClusterException {

    public ClusterDependencyCycleException(List<String> classNames) {
        super("Cyclic dependence in [" + String.join(", ", classNames) + "].");
    }
}
