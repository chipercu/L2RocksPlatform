package com.fuzzy.main.rdao.database.utils.key;

import com.fuzzy.main.rdao.database.exception.KeyCorruptedException;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.schema.HashIndex;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBHashIndex;
import com.fuzzy.main.rdao.database.utils.TypeConvert;

import java.nio.ByteBuffer;

import static com.fuzzy.main.rdao.database.schema.BaseIndex.ATTENDANT_BYTE_SIZE;

public class HashIndexKey extends IndexKey {

    private final long[] fieldValues;

    public HashIndexKey(long id, final HashIndex index) {
        this(id, index.attendant, new long[index.sortedFields.size()]);
    }

    public HashIndexKey(long id, final DBHashIndex index) {
        this(id, index.getAttendant(), new long[index.getFieldIds().length]);
    }

    public HashIndexKey(long id, final byte[] attendant, final long[] fieldValues) {
        super(id, attendant);

        if (fieldValues == null || fieldValues.length == 0) {
            throw new IllegalArgumentException();
        }
        this.fieldValues = fieldValues;
    }

    public long[] getFieldValues() {
        return fieldValues;
    }

    public byte[] getAttendant() {
        return attendant;
    }

    @Override
    public byte[] pack() {
        byte[] buffer = KeyUtils.allocateAndPutIndexAttendant(attendant.length + ID_BYTE_SIZE * fieldValues.length + ID_BYTE_SIZE,
                attendant);
        int offset = TypeConvert.pack(fieldValues, buffer, attendant.length);
        TypeConvert.pack(getId(), buffer, offset);
        return buffer;
    }

    public static HashIndexKey unpack(final byte[] src) {
        final int longCount = readLongCount(src);

        ByteBuffer buffer = TypeConvert.wrapBuffer(src);
        byte[] attendant = new byte[ATTENDANT_BYTE_SIZE];
        buffer.get(attendant);

        long[] fieldValues = new long[longCount - 1];
        for (int i = 0; i < fieldValues.length; ++i) {
            fieldValues[i] = buffer.getLong();
        }
        return new HashIndexKey(buffer.getLong(), attendant, fieldValues);
    }

    public static long unpackId(final byte[] src) {
        return TypeConvert.unpackLong(src, src.length - ID_BYTE_SIZE);
    }

    public static long unpackFirstIndexedValue(final byte[] src) {
        return TypeConvert.unpackLong(src, ATTENDANT_BYTE_SIZE);
    }

    public static KeyPattern buildKeyPattern(final HashIndex index, final long[] fieldValues) {
        byte[] buffer = KeyUtils.allocateAndPutIndexAttendant(index.attendant.length + ID_BYTE_SIZE * fieldValues.length,
                index.attendant);
        TypeConvert.pack(fieldValues, buffer, index.attendant.length);
        return new KeyPattern(buffer);
    }

    public static KeyPattern buildKeyPattern(final DBHashIndex index, final long[] fieldValues) {
        byte[] buffer = KeyUtils.allocateAndPutIndexAttendant(index.getAttendant().length + ID_BYTE_SIZE * fieldValues.length,
                index.getAttendant());
        TypeConvert.pack(fieldValues, buffer, index.getAttendant().length);
        return new KeyPattern(buffer);
    }

    public static KeyPattern buildKeyPattern(final DBHashIndex index, final long fieldValue) {
        byte[] buffer = KeyUtils.allocateAndPutIndexAttendant(index.getAttendant().length + ID_BYTE_SIZE,
                index.getAttendant());
        TypeConvert.pack(fieldValue, buffer, index.getAttendant().length);
        return new KeyPattern(buffer);
    }

    public static KeyPattern buildKeyPattern(final HashIndex index, final long fieldValue) {
        byte[] buffer = KeyUtils.allocateAndPutIndexAttendant(index.attendant.length + ID_BYTE_SIZE,
                index.attendant);
        TypeConvert.pack(fieldValue, buffer, index.attendant.length);
        return new KeyPattern(buffer);
    }

    public static KeyPattern buildKeyPatternForLastKey(final HashIndex index) {
        byte[] buffer = KeyUtils.allocateAndPutIndexAttendant(index.attendant.length + ID_BYTE_SIZE,
                index.attendant);
        TypeConvert.pack(0xFFFFFFFFFFFFFFFFL, buffer, index.attendant.length);
        return new KeyPattern(buffer, index.attendant.length);
    }

    private static int readLongCount(final byte[] src) {
        final int fieldsByteSize = src.length - ATTENDANT_BYTE_SIZE;
        final int count = fieldsByteSize / ID_BYTE_SIZE;
        final int tail = fieldsByteSize % ID_BYTE_SIZE;
        if (count < 2 || tail != 0) {
            throw new KeyCorruptedException(src);
        }
        return count;
    }
}
