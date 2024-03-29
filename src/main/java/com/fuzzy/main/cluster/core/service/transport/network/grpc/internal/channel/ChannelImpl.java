package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel;

import com.fuzzy.main.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.struct.RNode;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.PackageLog;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.convert.ConvertProto;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackageUpdateNode;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public abstract class ChannelImpl implements Channel {

    private final static Logger log = LoggerFactory.getLogger(ChannelImpl.class);

    public final RNode remoteNode;
    protected final StreamObserver<PNetPackage> requestObserver;

    private volatile boolean available;

    private final UUID uuid;

    protected ChannelImpl(UUID uuid, RNode remoteNode, StreamObserver<PNetPackage> requestObserver) {
        this.uuid = uuid;
        this.remoteNode = remoteNode;
        this.requestObserver = requestObserver;
        this.available = true;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public RNode getRemoteNode() {
        return remoteNode;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void handleIncomingPacket(PNetPackageUpdateNode value){
        List<LocationRuntimeComponent> components = ConvertProto.convert(remoteNode.node.getRuntimeId(), value);
        remoteNode.setComponents(components);
    }

    public void sent(PNetPackage value){
        if (log.isTraceEnabled()) {
            log.trace("Send packet: {} to channel: {}", PackageLog.toString(value), this);
        }
        synchronized (this) {
            try {
                requestObserver.onNext(value);
            } catch (Exception e) {
                requestObserver.onError(e);
                throw e;
            }
        }
    }

    public void destroy(){
        available = false;
    }

    @Override
    public String toString() {
        return "Channel{uuid: " + uuid + ", node: " + remoteNode.node.toString() + ", type: " + getType() + '}';
    }
}
