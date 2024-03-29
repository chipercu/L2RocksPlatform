package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.engine.server;

import com.infomaximum.cluster.core.service.transport.network.grpc.exception.ClusterGrpcException;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.pservice.PServiceExchangeImpl;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcServer implements AutoCloseable {

    public final GrpcNetworkTransitImpl grpcNetworkTransit;
    private final Channels channels;

    private final int port;

    private final byte[] fileCertChain;
    private final byte[] filePrivateKey;
    private final TrustManagerFactory trustStore;

    private Server server;

    public GrpcServer(GrpcNetworkTransitImpl grpcNetworkTransit, Channels channels, int port, byte[] fileCertChain, byte[] filePrivateKey, TrustManagerFactory trustStore) {
        this.grpcNetworkTransit = grpcNetworkTransit;
        this.channels = channels;
        this.port = port;

        this.fileCertChain = fileCertChain;
        this.filePrivateKey = filePrivateKey;
        this.trustStore = trustStore;
    }

    public void start() {
        ServerBuilder serverBuilder;
        if (filePrivateKey != null) {
            SslContext sslContext;
            try {
                sslContext = GrpcSslContexts
                        .forServer(new ByteArrayInputStream(fileCertChain), new ByteArrayInputStream(filePrivateKey))
                        .trustManager(trustStore)//Необходимо передавать клиенские сертификаты для валидации
//                        .trustManager()//Попытаться найти способ передачи отозванных сертефикатов- в крайнем случае можно обойтись
                        .clientAuth(ClientAuth.REQUIRE)
                        .build();
            } catch  (IOException e) {
                throw new ClusterGrpcException(e);
            }
            serverBuilder = NettyServerBuilder.forPort(port);
            ((NettyServerBuilder) serverBuilder).sslContext(sslContext);
        } else {
            serverBuilder = ServerBuilder.forPort(port);
        }

        serverBuilder
                .intercept(new GrpcServerExceptionHandler(grpcNetworkTransit.getUncaughtExceptionHandler()))
                .addService(new PServiceExchangeImpl(this, channels));
        try {
            server = serverBuilder.build().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        server.shutdownNow();
        try {
            server.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }
}
