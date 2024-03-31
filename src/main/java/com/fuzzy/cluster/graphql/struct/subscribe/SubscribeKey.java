package com.fuzzy.cluster.graphql.struct.subscribe;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.struct.Component;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class SubscribeKey implements RemoteObject {

    private final byte[] key;

    public SubscribeKey(Component component, byte[] subscribeKey) {
        this(component.getRemotes().cluster.node.getRuntimeId(), component.getId(), subscribeKey);
    }

    public SubscribeKey(UUID nodeRuntimeId, int componentId, byte[] subscribeKey) {
        key = ByteBuffer.allocate(Long.BYTES + Long.BYTES + Integer.BYTES + subscribeKey.length)
                .putLong(nodeRuntimeId.getMostSignificantBits())
                .putLong(nodeRuntimeId.getLeastSignificantBits())
                .putInt(componentId)
                .put(subscribeKey)
                .array();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SubscribeKey)) {
            return false;
        }
        return Arrays.equals(key, ((SubscribeKey) other).key);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }
}
