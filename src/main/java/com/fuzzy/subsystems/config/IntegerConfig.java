package com.fuzzy.subsystems.config;

public class IntegerConfig extends Config<Integer> {

    public IntegerConfig(String name, Integer defaultValue) {
        super(name, defaultValue, Integer.class);
    }
}
