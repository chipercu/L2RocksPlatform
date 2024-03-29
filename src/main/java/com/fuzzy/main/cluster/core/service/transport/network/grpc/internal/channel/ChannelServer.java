package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel;

import com.infomaximum.cluster.core.service.transport.network.grpc.internal.struct.RNode;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.convert.ConvertProto;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackageHandshakeRequest;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class ChannelServer extends ChannelImpl {

    ChannelServer(UUID uuid, RNode remoteNode, StreamObserver<PNetPackage> requestObserver) {
        super(uuid, remoteNode, requestObserver);
    }

    @Override
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        ServerCallStreamObserver<PNetPackage> serverCallStreamObserver = (ServerCallStreamObserver<PNetPackage>) requestObserver;
        return serverCallStreamObserver.isReady();
    }

    @Override
    public ChannelType getType() {
        return ChannelType.SERVER;
    }

    public static class Builder {

        private final StreamObserver<PNetPackage> requestObserver;
        private final PNetPackageHandshakeRequest handshakeRequest;

        public Builder(StreamObserver<PNetPackage> requestObserver, PNetPackageHandshakeRequest handshakeRequest) {
            this.requestObserver = requestObserver;
            this.handshakeRequest = handshakeRequest;
        }

        public ChannelServer build(){
            UUID uuidChannel = new UUID(handshakeRequest.getChannelIdMostSigBits(), handshakeRequest.getChannelIdLeastSigBit());
            RNode remoteNode = ConvertProto.convert(handshakeRequest.getNode());
            return new ChannelServer(uuidChannel, remoteNode, requestObserver);
        }
    }
}
