package com.fuzzy.subsystems.config;

import com.fuzzy.main.rdao.database.schema.TypeConverter;

public abstract class Config<T> {

    private final String name;

    private final T defaultValue;

    private final Class<T> type;

    Config(final String name, final T defaultValue, final Class<T> type) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Class<T> getType() {
        return type;
    }

    public TypeConverter<T> getConverter() {
        return null;
    }
}
