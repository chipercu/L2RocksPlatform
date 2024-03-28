package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.channel;

import com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.struct.RNode;

import java.util.UUID;

public interface Channel {

    UUID getUuid();

    RNode getRemoteNode();

    boolean isAvailable();

    ChannelType getType();

}
