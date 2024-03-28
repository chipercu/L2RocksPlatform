package com.fuzzy.subsystems.config;

public class LongConfig extends Config<Long> {

    public LongConfig(String name, Long defaultValue) {
        super(name, defaultValue, Long.class);
    }
}
