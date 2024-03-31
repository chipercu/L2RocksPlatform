package com.fuzzy.platform.exception;

import com.fuzzy.cluster.exception.ExceptionBuilder;
import com.fuzzy.platform.exception.ExceptionFactory;
import com.fuzzy.platform.exception.GeneralExceptionFactory;
import com.fuzzy.platform.exception.PlatformException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClusterExceptionBuilder implements ExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    @Override
    public Class getTypeException() {
        return PlatformException.class;
    }

    @Override
    public Exception buildTransitRequestException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("node_runtime_id", nodeRuntimeId.toString());
        return EXCEPTION_FACTORY.build(
                "remote_component_transit_request",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey,
                parameters, cause
        );
    }

    @Override
    public Exception buildRemoteComponentUnavailableException(UUID nodeRuntimeId, Exception cause) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("node_runtime_id", nodeRuntimeId.toString());
        return EXCEPTION_FACTORY.build(
                "remote_component_unavailable",
                "node: " + nodeRuntimeId,
                parameters, cause
        );
    }

    @Override
    public Exception buildRemoteComponentUnavailableException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey, Exception cause) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("node_runtime_id", nodeRuntimeId.toString());
        return EXCEPTION_FACTORY.build(
                "remote_component_unavailable",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey,
                parameters, cause
        );
    }

    @Override
    public Exception buildRemoteComponentNotFoundException(UUID nodeRuntimeId, int componentId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("node_runtime_id", nodeRuntimeId.toString());
        return EXCEPTION_FACTORY.build(
                "remote_component_not_found",
                "node: " + nodeRuntimeId + ", componentId: " + componentId,
                parameters
        );
    }

    @Override
    public Exception buildMismatchRemoteApiNotFoundControllerException(UUID nodeRuntimeId, int componentId, String rControllerClassName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("node_runtime_id", nodeRuntimeId.toString());
        return EXCEPTION_FACTORY.build(
                "mismatch_remote_api_not_found_controller",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName,
                parameters
        );
    }

    @Override
    public Exception buildMismatchRemoteApiNotFoundMethodException(UUID nodeRuntimeId, int componentId, String rControllerClassName, int methodKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("node_runtime_id", nodeRuntimeId.toString());
        return EXCEPTION_FACTORY.build(
                "mismatch_remote_api_not_found_method",
                "node: " + nodeRuntimeId + ", componentId: " + componentId + ", rControllerClassName: " + rControllerClassName + ", methodKey: " + methodKey,
                parameters
        );
    }
}
