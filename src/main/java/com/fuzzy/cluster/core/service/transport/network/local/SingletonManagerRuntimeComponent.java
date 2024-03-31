package com.fuzzy.cluster.core.service.transport.network.local;

import com.fuzzy.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.cluster.core.service.transport.network.LocationRuntimeComponent;
import com.fuzzy.cluster.core.service.transport.network.ManagerRuntimeComponent;
import com.fuzzy.cluster.core.service.transport.network.local.LocalManagerRuntimeComponent;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class SingletonManagerRuntimeComponent implements ManagerRuntimeComponent {

    private final UUID nodeRuntimeId;
    private final com.fuzzy.cluster.core.service.transport.network.local.LocalManagerRuntimeComponent localManagerRuntimeComponent;

    public SingletonManagerRuntimeComponent(UUID nodeRuntimeId) {
        this.nodeRuntimeId = nodeRuntimeId;
        this.localManagerRuntimeComponent = new com.fuzzy.cluster.core.service.transport.network.local.LocalManagerRuntimeComponent();
    }

    @Override
    public LocationRuntimeComponent find(String uuid) {
        RuntimeComponentInfo runtimeComponentInfo = localManagerRuntimeComponent.find(uuid);
        if (runtimeComponentInfo != null) {
            return new LocationRuntimeComponent(nodeRuntimeId, runtimeComponentInfo);
        } else {
            return null;
        }
    }

    @Override
    public LocationRuntimeComponent get(UUID nodeRuntimeId, int componentId) {
        if (!this.nodeRuntimeId.equals(nodeRuntimeId)) {
            return null;
        }
        RuntimeComponentInfo runtimeComponentInfo = localManagerRuntimeComponent.get(componentId);
        if (runtimeComponentInfo == null) {
            return null;
        }
        return new LocationRuntimeComponent(nodeRuntimeId, runtimeComponentInfo);
    }

    @Override
    public Collection<LocationRuntimeComponent> gets(UUID nodeRuntimeId) {
        if (!this.nodeRuntimeId.equals(nodeRuntimeId)) {
            return null;
        }
        return localManagerRuntimeComponent.getComponents().stream()
                .map(componentInfo -> new LocationRuntimeComponent(nodeRuntimeId, componentInfo))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<LocationRuntimeComponent> find(Class<? extends RController> remoteControllerClazz) {
        return localManagerRuntimeComponent.find(remoteControllerClazz).stream()
                .map(componentInfo -> new LocationRuntimeComponent(nodeRuntimeId, componentInfo))
                .collect(Collectors.toList());
    }

    @Override
    public LocalManagerRuntimeComponent getLocalManagerRuntimeComponent() {
        return localManagerRuntimeComponent;
    }
}
