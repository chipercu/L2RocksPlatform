package com.fuzzy.main.cluster;

import com.fuzzy.main.cluster.core.service.transport.TransportManager;
import com.fuzzy.main.cluster.core.service.transport.network.ManagerRuntimeComponent;
import com.fuzzy.main.cluster.core.service.transport.network.RemoteControllerRequest;

import java.util.List;

public interface NetworkTransit {

    Node getNode();

    ManagerRuntimeComponent getManagerRuntimeComponent();

    RemoteControllerRequest getRemoteControllerRequest();

    List<Node> getRemoteNodes();

    void start();

    void close();

    abstract class Builder {
        public abstract NetworkTransit build(TransportManager transportManager);
    }
}
