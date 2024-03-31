package com.fuzzy.cluster.core.service.transport.network.grpc;

import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.core.service.transport.network.grpc.GrpcNetworkTransit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class GrpcNode implements Node {

    private final String name;
    private final UUID runtimeId;

    private GrpcNode(String name, UUID runtimeId) {
        this.name = name;
        this.runtimeId = runtimeId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getRuntimeId() {
        return runtimeId;
    }

    @Override
    public String toString() {
        return "GrpcNode{" +
                "name='" + name + '\'' +
                ", runtimeId=" + runtimeId +
                '}';
    }

    public static class Builder {

        private String name;
        private UUID runtimeId;

        public Builder(GrpcNetworkTransit.Builder.Server server) {
            try {
                this.name = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                this.name = "unknownhost";
            }
            if (server != null) {
                this.name += ":" + server.port();
            }

            this.runtimeId = UUID.randomUUID();
        }

        public Builder(String name, UUID runtimeId) {
            this.name = name;
            this.runtimeId = runtimeId;
        }

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public GrpcNode build(){
            return new GrpcNode(name, runtimeId);
        }
    }
}
