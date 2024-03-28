package com.fuzzy.subsystems.config;

public class BooleanConfig extends Config<Boolean> {

    public BooleanConfig(String name, Boolean defaultValue) {
        super(name, defaultValue, Boolean.class);
    }
}
