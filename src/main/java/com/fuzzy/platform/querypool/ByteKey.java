package com.fuzzy.platform.querypool;

import java.util.Arrays;

class ByteKey {

    private final byte[] key;

    public ByteKey(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteKey byteKey = (ByteKey) o;
        return Arrays.equals(key, byteKey.key);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }
}
