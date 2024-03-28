package com.fuzzy.main.cluster.core.service.transport.network;

import com.fuzzy.main.cluster.core.service.transport.executor.ComponentExecutorTransport;
import com.fuzzy.main.cluster.struct.Component;

import java.util.UUID;

public interface RemoteControllerRequest {

    ComponentExecutorTransport.Result request(Component sourceComponent, UUID targetNodeRuntimeId, int targetComponentId, String rControllerClassName, int methodKey, byte[][] args) throws Exception;
}
