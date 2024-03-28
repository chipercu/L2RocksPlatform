package com.fuzzy.main.cluster.core.service.transport.network.grpc.exception;

public class ClusterGrpcException extends RuntimeException {

    public ClusterGrpcException() {
    }

    public ClusterGrpcException(String message) {
        super(message);
    }

    public ClusterGrpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClusterGrpcException(Throwable cause) {
        super(cause);
    }
}
