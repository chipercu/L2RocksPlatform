package com.fuzzy.database.utils.key;

import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.schema.BaseIntervalIndex;
import com.fuzzy.database.schema.dbstruct.DBBaseIntervalIndex;
import com.fuzzy.database.schema.dbstruct.DBRangeIndex;
import com.fuzzy.database.utils.TypeConvert;
import com.fuzzy.database.utils.key.IndexKey;

import java.nio.ByteBuffer;

public abstract class BaseIntervalIndexKey extends com.fuzzy.database.utils.key.IndexKey {

    private static final byte NEGATIVE_VALUE = 0;
    private static final byte POSITIVE_VALUE = 1;

    final long[] hashedValues;
    long indexedValue;

    BaseIntervalIndexKey(long id, long[] hashedValues, byte[] attendant) {
        super(id, attendant);
        if (hashedValues == null) {
            throw new IllegalArgumentException();
        }
        this.hashedValues = hashedValues;
    }

    public long[] getHashedValues() {
        return hashedValues;
    }

    public static long unpackId(byte[] src) {
        return TypeConvert.unpackLong(src, src.length - ID_BYTE_SIZE);
    }

    public static KeyPattern buildLeftBorder(long[] hashedValues, long indexedValue, final BaseIntervalIndex index) {
        ByteBuffer buffer = TypeConvert.allocateBuffer(index.attendant.length + ID_BYTE_SIZE * (hashedValues.length + 1) + Byte.BYTES);
        fillBuffer(index.attendant, hashedValues, indexedValue, buffer);
        return new KeyPattern(buffer.array(), index.attendant.length + ID_BYTE_SIZE * hashedValues.length);
    }

    public static KeyPattern buildLeftBorder(long[] hashedValues, long indexedValue, final DBBaseIntervalIndex index) {
        ByteBuffer buffer = TypeConvert.allocateBuffer(index.getAttendant().length + ID_BYTE_SIZE * (hashedValues.length + 1) + Byte.BYTES);
        fillBuffer(index.getAttendant(), hashedValues, indexedValue, buffer);
        return new KeyPattern(buffer.array(), index.getAttendant().length + ID_BYTE_SIZE * hashedValues.length);
    }

    public static KeyPattern buildLeftBorder(long[] hashedValues, long indexedValue, final DBRangeIndex index) {
        ByteBuffer buffer = TypeConvert.allocateBuffer(index.getAttendant().length + ID_BYTE_SIZE * (hashedValues.length + 1) + Byte.BYTES);
        fillBuffer(index.getAttendant(), hashedValues, indexedValue, buffer);
        return new KeyPattern(buffer.array(), index.getAttendant().length + ID_BYTE_SIZE * hashedValues.length);
    }

    public static KeyPattern buildRightBorder(long[] hashedValues, long indexedValue, final BaseIntervalIndex index) {
        ByteBuffer buffer = TypeConvert.allocateBuffer(index.attendant.length + ID_BYTE_SIZE * (hashedValues.length + 2) + Byte.BYTES);
        fillBuffer(index.attendant, hashedValues, indexedValue, buffer);
        buffer.putLong(0xffffffffffffffffL);
        KeyPattern pattern = new KeyPattern(buffer.array(), index.attendant.length + ID_BYTE_SIZE * hashedValues.length);
        pattern.setForBackward(true);
        return pattern;
    }

    public static KeyPattern buildRightBorder(long[] hashedValues, long indexedValue, final DBBaseIntervalIndex index) {
        ByteBuffer buffer = TypeConvert.allocateBuffer(index.getAttendant().length + ID_BYTE_SIZE * (hashedValues.length + 2) + Byte.BYTES);
        fillBuffer(index.getAttendant(), hashedValues, indexedValue, buffer);
        buffer.putLong(0xffffffffffffffffL);
        KeyPattern pattern = new KeyPattern(buffer.array(), index.getAttendant().length + ID_BYTE_SIZE * hashedValues.length);
        pattern.setForBackward(true);
        return pattern;
    }

    static void fillBuffer(byte[] attendant, long[] hashedValues, long indexedValue, ByteBuffer destination) {
        destination.put(attendant);
        for (long hashedValue : hashedValues) {
            destination.putLong(hashedValue);
        }
        destination.put(getSignByte(indexedValue));
        destination.putLong(indexedValue);
    }

    static Byte getSignByte(long value) {
        return value < 0 ? NEGATIVE_VALUE : POSITIVE_VALUE;
    }
}

