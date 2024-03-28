package com.fuzzy.main.cluster.exception;

import java.util.UUID;

public class ExceptionBuilderImpl implements ExceptionBuilder<ClusterException> {

    @Override
    public Class getTypeException() {
        return ClusterException.class;
    }

    @Override
    public ClusterException buildTransitRequestException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        return new ClusterException("TransitRequestException, nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
                + ", methodKey: " + methodKey, cause
        );
    }

    @Override
    public ClusterException buildRemoteComponentUnavailableException(UUID nodeRuntimeId, Exception cause) {
        return new ClusterException("RemoteComponentUnavailableException, nodeRuntimeId: " + nodeRuntimeId, cause);
    }

    @Override
    public ClusterException buildRemoteComponentUnavailableException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        return new ClusterException("RemoteComponentUnavailableException, nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
                + ", methodKey: " + methodKey, cause
        );
    }

    @Override
    public ClusterException buildRemoteComponentNotFoundException(UUID nodeRuntimeId, int componentId) {
        return new ClusterException("RemoteComponentUnavailableException, nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId
        );
    }

    @Override
    public ClusterException buildMismatchRemoteApiNotFoundControllerException(UUID nodeRuntimeId, int componentId, String rControllerClassName) {
        return new ClusterException("Mismatch api (not found controller), nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
        );
    }

    @Override
    public ClusterException buildMismatchRemoteApiNotFoundMethodException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey) {
        return new ClusterException("Mismatch api (not found method), nodeRuntimeId: " + nodeRuntimeId + ", componentUniqueId: "
                + componentId + ", rControllerClassName: " + rControllerClassName
                + ", methodKey: " + methodKey
        );
    }
}
