package com.fuzzy.main.rdao.database.utils.key;

import com.fuzzy.main.rdao.database.schema.BaseIntervalIndex;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBIntervalIndex;
import com.fuzzy.main.rdao.database.utils.IntervalIndexUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;

import java.nio.ByteBuffer;

public class IntervalIndexKey extends BaseIntervalIndexKey {

    public IntervalIndexKey(long id, final long[] hashedValues, BaseIntervalIndex index) {
        super(id, hashedValues, index.attendant);
    }

    public IntervalIndexKey(long id, final long[] hashedValues, DBIntervalIndex index) {
        super(id, hashedValues, index.getAttendant());
    }

    public void setIndexedValue(Object value) {
        indexedValue = IntervalIndexUtils.castToLong(value);
    }

    @Override
    public byte[] pack() {
        ByteBuffer buffer = TypeConvert.allocateBuffer(attendant.length + ID_BYTE_SIZE * (hashedValues.length + 2) + Byte.BYTES);
        fillBuffer(attendant, hashedValues, indexedValue, buffer);
        buffer.putLong(getId());
        return buffer.array();
    }

    public static long unpackIndexedValue(final byte[] src) {
        return TypeConvert.unpackLong(src, src.length - 2 * ID_BYTE_SIZE);
    }
}
