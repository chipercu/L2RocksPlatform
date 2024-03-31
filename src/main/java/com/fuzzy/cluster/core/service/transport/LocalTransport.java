package com.fuzzy.cluster.core.service.transport;

import com.fuzzy.cluster.Cluster;
import com.fuzzy.cluster.NetworkTransit;
import com.fuzzy.cluster.core.remote.RemoteTarget;
import com.fuzzy.cluster.core.remote.packer.RemotePackerObject;
import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.cluster.core.service.transport.TransportManager;
import com.fuzzy.cluster.core.service.transport.executor.ComponentExecutorTransport;
import com.fuzzy.cluster.struct.Component;

import java.lang.reflect.Method;

/**
 * Created by kris on 14.09.16.
 */
public class LocalTransport {

    private final TransportManager transportManager;

    private final Component component;
    private ComponentExecutorTransport componentExecutorTransport;

    public LocalTransport(TransportManager transportManager, Component component) {
        this.transportManager = transportManager;
        this.component = component;
    }

    public NetworkTransit getNetworkTransit() {
        return transportManager.networkTransit;
    }

    public Component getComponent() {
        return component;
    }

    public Cluster getCluster() {
        return transportManager.cluster;
    }

    public void setExecutor(ComponentExecutorTransport componentExecutorTransport) {
        this.componentExecutorTransport = componentExecutorTransport;
    }

    public ComponentExecutorTransport getExecutor() {
        return componentExecutorTransport;
    }

    public RemotePackerObject getRemotePackerObject() {
        return transportManager.getRemotePackerObject();
    }

    public Object request(RemoteTarget target, Class<? extends RController> rControllerClass, Method method, Object[] args) throws Throwable {
        return transportManager.request(component, target, rControllerClass, method, args);
    }
}
