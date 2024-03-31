package com.fuzzy.cluster.core.service.transport.network.grpc.internal;

import com.fuzzy.cluster.NetworkTransit;
import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.core.service.transport.TransportManager;
import com.fuzzy.cluster.core.service.transport.network.ManagerRuntimeComponent;
import com.fuzzy.cluster.core.service.transport.network.RemoteControllerRequest;
import com.fuzzy.cluster.core.service.transport.network.grpc.GrpcNetworkTransit;
import com.fuzzy.cluster.core.service.transport.network.grpc.GrpcNode;
import com.fuzzy.cluster.core.service.transport.network.grpc.GrpcRemoteNode;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.engine.GrpcManagerRuntimeComponent;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.engine.GrpcPoolExecutor;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.service.notification.NotificationUpdateComponent;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.service.remotecontroller.GrpcRemoteControllerRequest;
import com.fuzzy.cluster.core.remote.packer.RemotePackerObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManagerFactory;
import java.time.Duration;
import java.util.List;

public class GrpcNetworkTransitImpl implements NetworkTransit {

    private final static Logger log = LoggerFactory.getLogger(GrpcNetworkTransit.class);

    public final TransportManager transportManager;

    private final GrpcNode node;

    public final RemotePackerObject remotePackerObject;

    public final List<GrpcRemoteNode> targets;

    public final GrpcPoolExecutor grpcPoolExecutor;

    private final ManagerRuntimeComponent managerRuntimeComponent;
    private final GrpcRemoteControllerRequest remoteControllerRequest;

    private final Channels channels;

    private final Duration timeoutConfirmationWaitResponse;



    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;


    public GrpcNetworkTransitImpl(GrpcNetworkTransit.Builder builder, TransportManager transportManager, byte[] fileCertChain, byte[] filePrivateKey, TrustManagerFactory trustStore, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.transportManager = transportManager;
        this.remotePackerObject = transportManager.getRemotePackerObject();
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;

        GrpcNode.Builder nodeBuilder = new GrpcNode.Builder(builder.getServer());
        if (builder.getNodeName() != null) {
            nodeBuilder.withName(builder.getNodeName());
        }
        node = nodeBuilder.build();

        this.targets = builder.getTargets();
        this.timeoutConfirmationWaitResponse = builder.getTimeoutConfirmationWaitResponse();

        this.grpcPoolExecutor = new GrpcPoolExecutor(uncaughtExceptionHandler);

        this.remoteControllerRequest = new GrpcRemoteControllerRequest(this);
        this.channels = new Channels.Builder(this)
                .withSsl(fileCertChain, filePrivateKey, trustStore)
                .withServer(builder.getServer())
                .withTargets(targets)
                .withPingPongTimeout(builder.getPingPongInterval(), builder.getPingPongTimeout())
                .build();

        this.managerRuntimeComponent = new GrpcManagerRuntimeComponent(this, channels);
        new NotificationUpdateComponent(node, managerRuntimeComponent.getLocalManagerRuntimeComponent(), channels);
    }

    @Override
    public Node getNode() {
        return node;
    }

    public Channels getChannels() {
        return channels;
    }

    @Override
    public ManagerRuntimeComponent getManagerRuntimeComponent() {
        return managerRuntimeComponent;
    }

    @Override
    public RemoteControllerRequest getRemoteControllerRequest() {
        return remoteControllerRequest;
    }

    @Override
    public List<Node> getRemoteNodes() {
        return channels.getRemoteNodes();
    }

    public Duration getTimeoutConfirmationWaitResponse() {
        return timeoutConfirmationWaitResponse;
    }

    @Override
    public void start() {
        this.channels.start();
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }


    @Override
    public void close() {
        channels.close();
        remoteControllerRequest.close();
    }
}
