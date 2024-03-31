package com.fuzzy.cluster.graphql.struct;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

import java.io.Serializable;
import java.util.Optional;

public abstract class GSubscribeEvent<T extends Serializable> {

    public class SubscribeValue<T extends Serializable> implements RemoteObject {

        public final byte[] subscribeKey;
        public final Optional<T> value;

        public SubscribeValue(byte[] subscribeKey, T value) {
            this.subscribeKey = subscribeKey;
            this.value = Optional.ofNullable(value);
        }
    }

    private final SubscribeValue<T> value;

    public GSubscribeEvent(byte[] subscribeKey, T value) {
        this.value = new SubscribeValue(subscribeKey, value);
    }

    public SubscribeValue<T> getSubscribeValue() {
        return value;
    }
}
