package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.engine;

import com.fuzzy.main.cluster.Node;
import com.fuzzy.main.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.fuzzy.main.cluster.core.service.transport.network.ManagerRuntimeComponent;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.service.remotecomponent.RemoteManagerRuntimeComponent;
import com.fuzzy.main.cluster.core.service.transport.network.local.LocalManagerRuntimeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class GrpcManagerRuntimeComponent implements ManagerRuntimeComponent {

    private final static Logger log = LoggerFactory.getLogger(GrpcManagerRuntimeComponent.class);

    private final Node currentNode;
    public final LocalManagerRuntimeComponent localManagerRuntimeComponent;
    private final RemoteManagerRuntimeComponent remoteManagerRuntimeComponent;

    public GrpcManagerRuntimeComponent(GrpcNetworkTransitImpl grpcNetworkTransit, Channels channels) {
        this.currentNode = grpcNetworkTransit.getNode();
        this.localManagerRuntimeComponent = new LocalManagerRuntimeComponent();
        this.remoteManagerRuntimeComponent = new RemoteManagerRuntimeComponent(grpcNetworkTransit, channels);
    }


    @Override
    public LocationRuntimeComponent find(String uuid) {
        RuntimeComponentInfo runtimeComponentInfo = localManagerRuntimeComponent.find(uuid);
        if (runtimeComponentInfo != null) {
            return new LocationRuntimeComponent(currentNode.getRuntimeId(), runtimeComponentInfo);
        }
        return remoteManagerRuntimeComponent.find(uuid);
    }

    @Override
    public LocationRuntimeComponent get(UUID nodeRuntimeId, int componentId) {
        if (currentNode.getRuntimeId().equals(nodeRuntimeId)) {
            RuntimeComponentInfo runtimeComponentInfo = localManagerRuntimeComponent.get(componentId);
            if (runtimeComponentInfo == null) {
                return null;
            }
            return new LocationRuntimeComponent(nodeRuntimeId, runtimeComponentInfo);
        } else {
            return remoteManagerRuntimeComponent.get(nodeRuntimeId, componentId);
        }
    }

    @Override
    public Collection<LocationRuntimeComponent> gets(UUID nodeRuntimeId) {
        if (currentNode.getRuntimeId().equals(nodeRuntimeId)) {
            ArrayList<LocationRuntimeComponent> components = new ArrayList<>();
            for(RuntimeComponentInfo componentInfo: localManagerRuntimeComponent.getComponents()) {
                components.add(new LocationRuntimeComponent(nodeRuntimeId, componentInfo));
            }
            return components;
        } else {
            return remoteManagerRuntimeComponent.gets(nodeRuntimeId);
        }
    }

    @Override
    public Collection<LocationRuntimeComponent> find(Class<? extends RController> remoteControllerClazz) {
        ArrayList<LocationRuntimeComponent> components = new ArrayList<>();
        for(RuntimeComponentInfo componentInfo: localManagerRuntimeComponent.find(remoteControllerClazz)) {
            components.add(new LocationRuntimeComponent(currentNode.getRuntimeId(), componentInfo));
        }
        components.addAll(remoteManagerRuntimeComponent.find(remoteControllerClazz));
        return components;
    }

    @Override
    public LocalManagerRuntimeComponent getLocalManagerRuntimeComponent() {
        return localManagerRuntimeComponent;
    }
}
