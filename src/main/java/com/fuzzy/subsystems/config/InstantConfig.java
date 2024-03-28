package com.fuzzy.subsystems.config;

import com.fuzzy.main.rdao.database.schema.TypeConverter;
import com.fuzzy.main.rdao.database.utils.ByteUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;

import java.time.Instant;

public class InstantConfig extends Config<Instant> {

    public InstantConfig(String name, Instant defaultValue) {
        super(name, defaultValue, Instant.class);
    }

    @Override
    public TypeConverter<Instant> getConverter() {
        return new Converter();
    }

    static class Converter implements TypeConverter<Instant> {

        @Override
        public byte[] pack(Instant value) {
            return value != null ? TypeConvert.pack(value.getEpochSecond()) : TypeConvert.EMPTY_BYTE_ARRAY;
        }

        @Override
        public Instant unpack(byte[] value) {
            if (ByteUtils.isNullOrEmpty(value)) {
                return null;
            }
            return Instant.ofEpochSecond(TypeConvert.unpackLong(value));
        }

        @Override
        public long buildHash(Instant value) {
            return value != null ? value.getEpochSecond() : 0;
        }
    }
}
