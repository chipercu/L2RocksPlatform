package com.fuzzy.database.provider;

import java.io.Serializable;

public class KeyValue implements Serializable {

    private final byte[] key;
    private final byte[] value;

    public KeyValue(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }
}
