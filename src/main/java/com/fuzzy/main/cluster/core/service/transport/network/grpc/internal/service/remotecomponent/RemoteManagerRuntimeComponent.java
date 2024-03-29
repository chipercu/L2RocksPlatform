package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.service.remotecomponent;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.channel.Channel;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.channel.Channels;
import com.fuzzy.main.cluster.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class RemoteManagerRuntimeComponent {

    private final Channels channels;

    public RemoteManagerRuntimeComponent(GrpcNetworkTransitImpl grpcNetworkTransit, Channels channels) {
        this.channels = channels;
    }

    public LocationRuntimeComponent find(String uuid) {
        ArrayList<LocationRuntimeComponent> contenders = new ArrayList<>();
        for(LocationRuntimeComponent runtimeComponentInfo: channels.getComponents()) {
            if (runtimeComponentInfo.component().uuid.equals(uuid)) {
                contenders.add(runtimeComponentInfo);
            }
        }
        if (contenders.isEmpty()) {
            return null;
        } else {
            return contenders.get(RandomUtil.random.nextInt(contenders.size()));
        }
    }

    //TODO Не оптимально - лишние преобразования и поиск объектов
    public Collection<LocationRuntimeComponent> find(Class<? extends RController> remoteControllerClazz) {
        ArrayList<LocationRuntimeComponent> components = new ArrayList<>();
        for(LocationRuntimeComponent runtimeComponentInfo: channels.getComponents()) {
            if (runtimeComponentInfo.component().getClassNameRControllers().contains(remoteControllerClazz.getName())) {
                components.add(runtimeComponentInfo);
            }
        }
        return components;
    }

    public LocationRuntimeComponent get(UUID nodeRuntimeId, int componentId) {
        Channel channel = channels.getChannel(nodeRuntimeId);
        if (channel == null) {
            return null;
        }
        for(LocationRuntimeComponent runtimeComponentInfo: channel.getRemoteNode().getComponents()) {
            if (runtimeComponentInfo.component().id==componentId) {
                return runtimeComponentInfo;
            }
        }
        return null;
    }

    public Collection<LocationRuntimeComponent> gets(UUID nodeRuntimeId) {
        Channel channel = channels.getChannel(nodeRuntimeId);
        if (channel == null) {
            return null;
        }
        ArrayList<LocationRuntimeComponent> components = new ArrayList<>();
        for(LocationRuntimeComponent runtimeComponentInfo: channel.getRemoteNode().getComponents()) {
            components.add(runtimeComponentInfo);
        }
        return components;
    }
}
