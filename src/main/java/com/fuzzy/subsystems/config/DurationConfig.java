package com.fuzzy.subsystems.config;

import com.infomaximum.database.schema.TypeConverter;
import com.infomaximum.database.utils.ByteUtils;
import com.infomaximum.database.utils.TypeConvert;

import java.time.Duration;

public class DurationConfig extends Config<Duration> {

    public DurationConfig(String name, Duration defaultValue) {
        super(name, defaultValue, Duration.class);
    }

    @Override
    public TypeConverter<Duration> getConverter() {
        return new Converter();
    }

    static class Converter implements TypeConverter<Duration> {

        @Override
        public byte[] pack(Duration value) {
            return value != null ? TypeConvert.pack(value.toMillis()) : TypeConvert.EMPTY_BYTE_ARRAY;
        }

        @Override
        public Duration unpack(byte[] value) {
            if (ByteUtils.isNullOrEmpty(value)) {
                return null;
            }
            return Duration.ofMillis(TypeConvert.unpackLong(value));
        }

        @Override
        public long buildHash(Duration value) {
            return value != null ? value.toMillis() : 0;
        }
    }
}