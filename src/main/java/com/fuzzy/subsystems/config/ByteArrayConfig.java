package com.fuzzy.subsystems.config;

public class ByteArrayConfig extends Config<byte[]> {

    public ByteArrayConfig(String name, byte[] defaultValue) {
        super(name, defaultValue, byte[].class);
    }
}
