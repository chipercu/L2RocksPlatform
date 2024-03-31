package com.fuzzy.subsystems.config;

import com.fuzzy.database.schema.TypeConverter;
import com.fuzzy.database.utils.BaseEnum;
import com.fuzzy.database.utils.EnumConverter;

public class EnumConfig<T extends Enum<?> & BaseEnum> extends Config<T> {

    private final TypeConverter<T> converter;

    public EnumConfig(String name, T defaultValue, Class<T> type) {
        super(name, defaultValue, type);
        this.converter = new Converter<>(type);
    }

    @Override
    public TypeConverter<T> getConverter() {
        return converter;
    }

    static class Converter<T extends Enum<?> & BaseEnum> extends EnumConverter<T> {

        Converter(Class<T> clazz) {
            super(clazz);
        }
    }
}
