package com.fuzzy.subsystems.config;

public class StringConfig extends Config<String> {

    public StringConfig(String name, String defaultValue) {
        super(name, defaultValue, String.class);
    }
}
