package com.fuzzy.cluster.core.service.transport.network.local;

import com.fuzzy.cluster.NetworkTransit;
import com.fuzzy.cluster.Node;
import com.fuzzy.cluster.core.service.transport.TransportManager;
import com.fuzzy.cluster.core.service.transport.network.ManagerRuntimeComponent;
import com.fuzzy.cluster.core.service.transport.network.RemoteControllerRequest;
import com.fuzzy.cluster.core.service.transport.network.local.SingletonManagerRuntimeComponent;
import com.fuzzy.cluster.core.service.transport.network.local.SingletonNode;

import java.util.Collections;
import java.util.List;

public class LocalNetworkTransit implements NetworkTransit {

    private final Node node;

    private final ManagerRuntimeComponent managerRuntimeComponent;

    private LocalNetworkTransit() {
        this.node = new SingletonNode();
        this.managerRuntimeComponent = new SingletonManagerRuntimeComponent(node.getRuntimeId());
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public ManagerRuntimeComponent getManagerRuntimeComponent() {
        return managerRuntimeComponent;
    }

    @Override
    public RemoteControllerRequest getRemoteControllerRequest() {
        return null;
    }

    @Override
    public List<Node> getRemoteNodes() {
        return Collections.emptyList();
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {
    }

    public static class Builder extends NetworkTransit.Builder {

        @Override
        public LocalNetworkTransit build(TransportManager transportManager) {
            return new LocalNetworkTransit();
        }

    }
}
