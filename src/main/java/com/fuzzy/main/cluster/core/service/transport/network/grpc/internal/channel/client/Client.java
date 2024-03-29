package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.client;

import com.fuzzy.main.cluster.Node;
import com.infomaximum.cluster.core.service.transport.network.grpc.GrpcRemoteNode;
import com.infomaximum.cluster.core.service.transport.network.grpc.exception.ClusterGrpcException;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.engine.GrpcPoolExecutor;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.netpackage.NetPackageHandshakeCreator;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.service.remotecontroller.GrpcRemoteControllerRequest;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.MLogger;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.PackageLog;
import com.fuzzy.main.cluster.utils.ExecutorUtil;
import com.infomaximum.cluster.core.service.transport.network.grpc.pservice.PServiceExchangeGrpc;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Client implements AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(Client.class);

    private final static int TIMEOUT_REPEAT_CONNECT = 1000;//Пауза между попытками подключения(в миллисекундах)
    public final GrpcRemoteNode remoteNode;
    public final ManagedChannel channel;
    private final GrpcNetworkTransitImpl grpcNetworkTransit;
    private final GrpcRemoteControllerRequest remoteControllerRequest;
    private final Channels channels;
    private final PServiceExchangeGrpc.PServiceExchangeStub exchangeStub;
    private final StreamObserver<PNetPackage> responseObserver;
    private final MLogger mLog;

    private final GrpcPoolExecutor grpcPoolExecutor;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private volatile UUID channelUuid;
    private volatile com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.ChannelClient clientChannel;
    private volatile StreamObserver<PNetPackage> channelRequestObserver;

    private volatile boolean isClosed = false;

    public Client(GrpcNetworkTransitImpl grpcNetworkTransit, Channels channels, GrpcRemoteNode remoteNode, byte[] fileCertChain, byte[] filePrivateKey, TrustManagerFactory trustStore) {
        this.grpcNetworkTransit = grpcNetworkTransit;
        this.remoteControllerRequest = (GrpcRemoteControllerRequest) grpcNetworkTransit.getRemoteControllerRequest();
        this.channels = channels;
        this.remoteNode = remoteNode;
        this.grpcPoolExecutor = grpcNetworkTransit.grpcPoolExecutor;
        this.uncaughtExceptionHandler = grpcNetworkTransit.getUncaughtExceptionHandler();

        if (filePrivateKey == null) {
            channel = NettyChannelBuilder.forTarget(remoteNode.target)
                    .usePlaintext()
                    .enableRetry()
                    .build();
        } else {
            SslContext sslContext;
            try {
                sslContext = GrpcSslContexts.forClient().trustManager(trustStore)
                        .keyManager(new ByteArrayInputStream(fileCertChain), new ByteArrayInputStream(filePrivateKey))
                        .build();

            } catch (IOException e) {
                throw new ClusterGrpcException(e);
            }
            channel = NettyChannelBuilder.forTarget(remoteNode.target)
                    .sslContext(sslContext)
                    .enableRetry()
                    .build();
        }

        this.mLog = new MLogger(log, 60 * 1000 / TIMEOUT_REPEAT_CONNECT);//Раз в 1 минуту

        exchangeStub = PServiceExchangeGrpc.newStub(channel);
        responseObserver =
                new StreamObserver<PNetPackage>() {
                    @Override
                    public void onNext(PNetPackage requestPackage) {
                        try {
                            if (clientChannel != null && log.isTraceEnabled()) {
                                log.trace("Incoming packet: {} to channel: {}", PackageLog.toString(requestPackage), clientChannel);
                            }

                            if (clientChannel == null) {
                                clientChannel = initChannel(requestPackage);
                            } else {
                                grpcPoolExecutor.execute(() -> {
                                    try {
                                        handleIncomingPacket(requestPackage);
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
                    public void onError(Throwable t) {
                        try {
                            mLog.warn("Error connect to remote node: {}, exception: {}", remoteNode.target, t);
                            destroyChannel();

                            ExecutorUtil.executors.execute(() -> {
                                try {
                                    Thread.sleep(TIMEOUT_REPEAT_CONNECT);
                                    reconnect();
                                } catch (Throwable e) {
                                    grpcNetworkTransit.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                }
                            });
                        } catch (Throwable te) {
                            grpcNetworkTransit.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), te);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        try {
                            log.warn("Completed connection with remote node: {}, repeat...", remoteNode.target);
                            destroyChannel();

                            ExecutorUtil.executors.execute(() -> {
                                try {
                                    Thread.sleep(TIMEOUT_REPEAT_CONNECT);
                                    reconnect();
                                } catch (Throwable e) {
                                    grpcNetworkTransit.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                }
                            });
                        } catch (Throwable te) {
                            grpcNetworkTransit.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), te);
                        }
                    }
                };
    }

    private void reconnect() {
        //Отзываем невалидный канал
        destroyChannel();

        if (isClosed) return;

        //Инициализируем
        channelUuid = UUID.randomUUID();
        channelRequestObserver = exchangeStub.exchange(responseObserver);
        PNetPackage packageHandshake = NetPackageHandshakeCreator.createRequest(grpcNetworkTransit, channelUuid);
        channelRequestObserver.onNext(packageHandshake);
    }

    private void destroyChannel() {
        if (clientChannel != null) {
            channels.unRegisterChannel(clientChannel);
            clientChannel = null;
        }
    }

    public void start() {
        reconnect();
    }

    private com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.ChannelClient initChannel(PNetPackage requestPackage) {
        if (!requestPackage.hasHandshakeResponse()) {
            log.error("Unknown state, channel: null, packet: {}. Disconnect", requestPackage.toString());
            //TODO надо переподнимать соединение, а не падать
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), new RuntimeException("Unknown state"));
            return null;
        }

        com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.ChannelClient clientChannel = new com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.ChannelClient.Builder(channelUuid, channelRequestObserver, requestPackage.getHandshakeResponse()).build();

        //Проверяем, что не подключились к себе же
        Node currentNode = grpcNetworkTransit.getNode();
        Node channelRemoteNode = clientChannel.remoteNode.node;
        if (currentNode.getRuntimeId().equals(channelRemoteNode.getRuntimeId())) {
            log.error("Loop connect, disconnect: {},", remoteNode.target);
            isClosed = true;
            channel.shutdownNow();
            return null;
        }

        channels.registerChannel(clientChannel);

        if (log.isTraceEnabled()) {
            log.trace("Incoming packet: {} to channel: {}", PackageLog.toString(requestPackage), clientChannel);
        }

        //За то время пока устанавливалось соединение могли загрузится новые компоненты - стоит повторно отправить свое состояние
        PNetPackage netPackageUpdateNode = NetPackageHandshakeCreator.buildPacketUpdateNode(grpcNetworkTransit.getManagerRuntimeComponent().getLocalManagerRuntimeComponent());
        channelRequestObserver.onNext(netPackageUpdateNode);

        return clientChannel;
    }

    private void handleIncomingPacket(PNetPackage requestPackage) {
        if (requestPackage.hasRequest()) {//Пришел запрос
            remoteControllerRequest.handleIncomingPacket(requestPackage.getRequest(), clientChannel);
        } else if (requestPackage.hasResponse()) {//Пришел ответ
            remoteControllerRequest.handleIncomingPacket(requestPackage.getResponse());
        } else if (requestPackage.hasResponseProcessing()) {
            remoteControllerRequest.handleIncomingPacket(requestPackage.getResponseProcessing());
        } else if (requestPackage.hasUpdateNode()) {
            clientChannel.handleIncomingPacket(requestPackage.getUpdateNode());
        } else {
            log.error("Unknown state, channel: {}, packet: {}. Disconnect", clientChannel, requestPackage.toString());
            //TODO надо переподнимать соединение, а не падать
            grpcNetworkTransit.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), new RuntimeException("TODO: need reconnect"));
        }
    }

    @Override
    public void close() {
        isClosed = true;
        try {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
    }

}
