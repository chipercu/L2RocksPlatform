package com.fuzzy.main.cluster.core.service.transport.network.grpc;

public class GrpcRemoteNode {

    public final String target;

    private GrpcRemoteNode(Builder builder) {
        this.target = builder.target;
    }

    public static class Builder {

        private String target;

        public Builder(String target) {
            this.target = target;
        }

        public GrpcRemoteNode build() {
            return new GrpcRemoteNode(this);
        }
    }
}
