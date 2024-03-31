package com.fuzzy.cluster.exception;

import java.util.UUID;

public class ExceptionBuilderImpl implements ExceptionBuilder<com.fuzzy.cluster.exception.ClusterException> {

    @Override
    public Class getTypeException() {
        return com.fuzzy.cluster.exception.ClusterException.class;
    }

    @Override
    public com.fuzzy.cluster.exception.ClusterException buildTransitRequestException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        return new com.fuzzy.cluster.exception.ClusterException("TransitRequestException, nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
                + ", methodKey: " + methodKey, cause
        );
    }

    @Override
    public com.fuzzy.cluster.exception.ClusterException buildRemoteComponentUnavailableException(UUID nodeRuntimeId, Exception cause) {
        return new com.fuzzy.cluster.exception.ClusterException("RemoteComponentUnavailableException, nodeRuntimeId: " + nodeRuntimeId, cause);
    }

    @Override
    public com.fuzzy.cluster.exception.ClusterException buildRemoteComponentUnavailableException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        return new com.fuzzy.cluster.exception.ClusterException("RemoteComponentUnavailableException, nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
                + ", methodKey: " + methodKey, cause
        );
    }

    @Override
    public com.fuzzy.cluster.exception.ClusterException buildRemoteComponentNotFoundException(UUID nodeRuntimeId, int componentId) {
        return new com.fuzzy.cluster.exception.ClusterException("RemoteComponentUnavailableException, nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId
        );
    }

    @Override
    public com.fuzzy.cluster.exception.ClusterException buildMismatchRemoteApiNotFoundControllerException(UUID nodeRuntimeId, int componentId, String rControllerClassName) {
        return new com.fuzzy.cluster.exception.ClusterException("Mismatch api (not found controller), nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
        );
    }

    @Override
    public com.fuzzy.cluster.exception.ClusterException buildMismatchRemoteApiNotFoundMethodException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey) {
        return new ClusterException("Mismatch api (not found method), nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
                + ", methodKey: " + methodKey
        );
    }
}
