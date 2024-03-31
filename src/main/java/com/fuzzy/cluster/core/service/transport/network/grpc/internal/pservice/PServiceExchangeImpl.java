package com.fuzzy.cluster.core.service.transport.network.grpc.internal.pservice;

import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.core.service.transport.TransportManager;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelImpl;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.ChannelServer;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.engine.GrpcPoolExecutor;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.netpackage.NetPackageHandshakeCreator;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.service.remotecontroller.GrpcRemoteControllerRequest;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.PackageLog;
import com.infomaximum.cluster.core.service.transport.network.grpc.pservice.PServiceExchangeGrpc;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackageHandshakeRequest;
import com.fuzzy.cluster.event.CauseNodeDisconnect;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PServiceExchangeImpl extends PServiceExchangeGrpc.PServiceExchangeImplBase {

    private final static Logger log = LoggerFactory.getLogger(PServiceExchangeImpl.class);

    private final GrpcRemoteControllerRequest remoteControllerRequest;
    private final TransportManager transportManager;
    private final Channels channels;
    private final GrpcPoolExecutor grpcPoolExecutor;

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public PServiceExchangeImpl(Channels channels) {
        GrpcNetworkTransitImpl grpcNetworkTransit = (GrpcNetworkTransitImpl) channels.transportManager.networkTransit;
        this.remoteControllerRequest = (GrpcRemoteControllerRequest) grpcNetworkTransit.getRemoteControllerRequest();
        this.transportManager = channels.transportManager;
        this.channels = channels;
        this.uncaughtExceptionHandler = channels.getUncaughtExceptionHandler();
        this.grpcPoolExecutor = grpcNetworkTransit.grpcPoolExecutor;
    }


    @Override
    public StreamObserver<PNetPackage> exchange(StreamObserver<PNetPackage> responseObserver) {
        final ChannelServer[] serverChannel = {null};

        StreamObserver<PNetPackage> requestObserver = new StreamObserver<PNetPackage>() {

            @Override
            public void onNext(PNetPackage requestPackage) {
                try {
                    if (serverChannel[0] != null && log.isTraceEnabled()) {
                        log.trace("Incoming packet: {} to channel: {}", PackageLog.toString(requestPackage), serverChannel[0]);
                    }

                    if (serverChannel[0] == null) {
                        serverChannel[0] = initChannel(responseObserver, requestPackage);
                    } else {
                        grpcPoolExecutor.execute(() -> {
                            try {
                                handleIncomingPacket(serverChannel[0], requestPackage);
                            } catch (Throwable t) {
                                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                            }
                        });
                    }
                } catch (Throwable t) {
                    uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                CauseNodeDisconnect.Type typeCause = CauseNodeDisconnect.Type.EXCEPTION;
                try {
                    destroyChannel(serverChannel, new CauseNodeDisconnect(typeCause, throwable));
                    log.error("onError", throwable);
                } catch (Throwable t) {
                    uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }

            @Override
            public void onCompleted() {
                try {
                    destroyChannel(serverChannel, CauseNodeDisconnect.NORMAL);
                    log.error("onCompleted");
                } catch (Throwable t) {
                    uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
                }
            }
        };
        return requestObserver;
    }

    private ChannelServer initChannel(StreamObserver<PNetPackage> responseObserver, PNetPackage requestPackage) {
        if (!requestPackage.hasHandshakeRequest()) {
            log.error("Unknown state, channel: null, packet: {}. Disconnect", requestPackage.toString());
            //TODO надо переподнимать соединение, а не падать
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), new RuntimeException("Unknown state"));
            return null;
        }

        //Сообщаем о себе и регистрируем канал
        PNetPackageHandshakeRequest handshakeRequest = requestPackage.getHandshakeRequest();

        PNetPackage handshakeResponse = NetPackageHandshakeCreator.createResponse((GrpcNetworkTransitImpl) transportManager.networkTransit);
        responseObserver.onNext(handshakeResponse);

        ChannelServer channelServer = new ChannelServer.Builder(responseObserver, handshakeRequest).build();

        Node currentNode = transportManager.cluster.node;
        Node channelRemoteNode = channelServer.remoteNode.node;
        if (currentNode.getRuntimeId().equals(channelRemoteNode.getRuntimeId())) {
            log.error("Loop connect: ignore channel");
            responseObserver.onCompleted();
            return null;
        }

        channels.registerChannel(channelServer);
        log.trace("Incoming packet: {} to channel: {}", PackageLog.toString(requestPackage), channelServer);
        return channelServer;
    }


    private void handleIncomingPacket(ChannelServer channelServer, PNetPackage requestPackage) {
        if (requestPackage.hasRequest()) {
            remoteControllerRequest.handleIncomingPacket(requestPackage.getRequest(), channelServer);
        } else if (requestPackage.hasResponse()) {
            remoteControllerRequest.handleIncomingPacket(requestPackage.getResponse());
        } else if (requestPackage.hasResponseProcessing()) {
            remoteControllerRequest.handleIncomingPacket(requestPackage.getResponseProcessing());
        } else if (requestPackage.hasUpdateNode()) {
            channelServer.handleIncomingPacket(requestPackage.getUpdateNode());
        } else if (requestPackage.hasPing()) {
            channels.getPingPongService().handleIncomingPing(channelServer, requestPackage.getPing());
        } else if (requestPackage.hasPong()) {
            channels.getPingPongService().handleIncomingPong(channelServer, requestPackage.getPong());
        } else {
            log.error("Unknown state, channel: {}, packet: {}. Disconnect", channelServer, requestPackage.toString());
            //TODO надо переподнимать соединение, а не падать
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), new RuntimeException("Unknown state"));
        }
    }

    private void destroyChannel(ChannelImpl[] serverChannel, CauseNodeDisconnect cause) {
        if (serverChannel[0] != null) {
            channels.unRegisterChannel(serverChannel[0], cause);
            serverChannel[0] = null;
        }
    }
}
