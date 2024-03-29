package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.client;

import com.infomaximum.cluster.core.service.transport.network.grpc.GrpcRemoteNode;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.channel.ChannelList;

import javax.net.ssl.TrustManagerFactory;
import java.util.List;

public class Clients implements AutoCloseable {

    private final Client[] clients;

    public Clients(GrpcNetworkTransitImpl grpcNetworkTransit, ChannelList channelList, List<GrpcRemoteNode> targets, byte[] fileCertChain, byte[] filePrivateKey, TrustManagerFactory trustStore) {
        this.clients = new Client[targets.size()];
        for (int i = 0; i < clients.length; i++) {
            GrpcRemoteNode target = targets.get(i);
            this.clients[i] = new Client(grpcNetworkTransit, channelList.channels, target, fileCertChain, filePrivateKey, trustStore);
        }
    }

    public void start() {
        for (Client client: clients) {
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
