package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel;

import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.struct.RNode;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.utils.convert.ConvertProto;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackageHandshakeResponse;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class ChannelClient extends ChannelImpl {

    ChannelClient(UUID uuid, RNode remoteNode, StreamObserver<PNetPackage> requestObserver) {
        super(uuid, remoteNode, requestObserver);
    }

    @Override
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        ClientCallStreamObserver<PNetPackage> clientCallStreamObserver = (ClientCallStreamObserver<PNetPackage>) requestObserver;
        return clientCallStreamObserver.isReady();
    }

    @Override
    public ChannelType getType() {
        return ChannelType.CLIENT;
    }

    public static class Builder {

        private final UUID uuid;
        private final StreamObserver<PNetPackage> requestObserver;
        private final PNetPackageHandshakeResponse handshakeResponse;

        public Builder(UUID uuid, StreamObserver<PNetPackage> requestObserver, PNetPackageHandshakeResponse handshakeResponse) {
            this.uuid = uuid;
            this.requestObserver = requestObserver;
            this.handshakeResponse = handshakeResponse;
        }

        public ChannelClient build(){
            RNode remoteNode = ConvertProto.convert(handshakeResponse.getNode());
            return new ChannelClient(uuid, remoteNode, requestObserver);
        }
    }
}
