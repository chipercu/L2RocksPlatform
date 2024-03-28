package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.service.notification;

import com.fuzzy.main.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.GrpcNode;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.netpackage.NetPackageHandshakeCreator;
import com.fuzzy.main.cluster.core.service.transport.network.local.LocalManagerRuntimeComponent;
import com.fuzzy.main.cluster.core.service.transport.network.local.event.EventUpdateLocalComponent;
import com.infomaximum.cluster.core.service.transport.network.grpc.struct.PNetPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationUpdateComponent implements EventUpdateLocalComponent {

    private final static Logger log = LoggerFactory.getLogger(NotificationUpdateComponent.class);

    private final GrpcNode node;
    private final LocalManagerRuntimeComponent localManagerRuntimeComponent;
    private final Channels channels;

    public NotificationUpdateComponent(GrpcNode node, LocalManagerRuntimeComponent localManagerRuntimeComponent, Channels channels) {
        this.node = node;
        this.localManagerRuntimeComponent = localManagerRuntimeComponent;
        this.channels = channels;
        localManagerRuntimeComponent.addListener(this);
    }

    @Override
    public void registerComponent(RuntimeComponentInfo subSystemInfo) {
        sentNotification();
    }

    @Override
    public void unRegisterComponent(RuntimeComponentInfo subSystemInfo) {
        sentNotification();
    }

    private void sentNotification() {
        PNetPackage netPackage = NetPackageHandshakeCreator.buildPacketUpdateNode(localManagerRuntimeComponent);
        channels.sendBroadcast(netPackage);
    }
}
