package com.fuzzy.main.cluster.core.service.transport.network.local;

import com.fuzzy.main.cluster.Node;

import java.util.UUID;

public class SingletonNode implements Node {

    private UUID runtimeId;

    public SingletonNode() {
        runtimeId = UUID.randomUUID();
    }

    @Override
    public String getName() {
        return "singleton";
    }

    @Override
    public UUID getRuntimeId() {
        return runtimeId;
    }
}
