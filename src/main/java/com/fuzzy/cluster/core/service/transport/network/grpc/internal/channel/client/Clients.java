package com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.client;

import com.fuzzy.cluster.core.service.transport.network.grpc.GrpcRemoteNode;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelList;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.client.Client;

import javax.net.ssl.TrustManagerFactory;
import java.util.List;

public class Clients implements AutoCloseable {

    private final com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.client.Client[] clients;

    public Clients(GrpcNetworkTransitImpl grpcNetworkTransit, ChannelList channelList, List<GrpcRemoteNode> targets, byte[] fileCertChain, byte[] filePrivateKey, TrustManagerFactory trustStore) {
        this.clients = new com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.client.Client[targets.size()];
        for (int i = 0; i < clients.length; i++) {
            GrpcRemoteNode target = targets.get(i);
            this.clients[i] = new com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.client.Client(grpcNetworkTransit, channelList.channels, target, fileCertChain, filePrivateKey, trustStore);
        }
    }

    public void start() {
        for (com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.client.Client client: clients) {
            client.start();
        }
    }

    @Override
    public void close() {
        for (Client client : clients) {
            client.close();
        }
    }

}
