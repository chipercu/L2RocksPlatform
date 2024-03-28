package com.fuzzy.main.rdao.database.utils;

import com.fuzzy.main.rdao.database.exception.UnsupportedTypeException;
import com.fuzzy.main.rdao.database.schema.TypeConverter;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedInteger;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.zip.CRC32;

public class TypeConvert {

    public static byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static byte NULL_BYTE_ARRAY_SCHIELD = (byte) 0xff;
    public static byte[] NULL_STRING = new byte[] { (byte) 0xff };

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static ByteBuffer allocateBuffer(int capacity) {
        return ByteBuffer.allocate(capacity).order(ByteOrder.BIG_ENDIAN);
    }

    public static ByteBuffer wrapBuffer(byte[] src) {
        return ByteBuffer.wrap(src).order(ByteOrder.BIG_ENDIAN);
    }

    public static String unpackString(byte[] value) {
        return unpackString(value, 0, value != null ? value.length : 0);
    }

    public static String unpackString(byte[] value, int offset, int length) {
        if (value == null || (length == 1 && value[offset] == NULL_STRING[0])) {
            return null;
        }

        return length != 0 ? new String(value, offset, length, CHARSET) : "";
    }

    public static Integer unpackInteger(byte[] value) {
        return !ByteUtils.isNullOrEmpty(value) ? unpackInt(value) : null;
    }

    public static int unpackInt(byte[] value) {
        return Ints.fromByteArray(value);
    }

    public static Long unpackLong(byte[] value) {
        return !ByteUtils.isNullOrEmpty(value) ? unpackLong(value, 0) : null;
    }

    public static long unpackLong(byte[] value, int offset) {
        return Longs.fromBytes(value[offset], value[1 + offset], value[2 + offset], value[3 + offset], value[4 + offset], value[5 + offset], value[6 + offset], value[7 + offset]);
    }

    public static Double unpackDouble(byte[] value) {
        return !ByteUtils.isNullOrEmpty(value) ? unpackDoublePrim(value) : null;
    }

    public static double unpackDoublePrim(byte[] value) {
        return Double.longBitsToDouble(unpackLong(value, 0));
    }

    public static Boolean unpackBoolean(byte[] value) {
        return !ByteUtils.isNullOrEmpty(value) ? value[0] == (byte) 1 : null;
    }

    public static Instant unpackInstant(byte[] value) {
        return !ByteUtils.isNullOrEmpty(value) ? InstantUtils.fromLong(Longs.fromByteArray(value)) : null;
    }

    public static LocalDateTime unpackLocalDateTime(byte[] value) {
        return !ByteUtils.isNullOrEmpty(value) ? LocalDateTimeUtils.fromLong(Longs.fromByteArray(value)) : null;
    }

    public static byte[] pack(String value){
        return value != null ? (value.isEmpty() ? EMPTY_BYTE_ARRAY : value.getBytes(CHARSET)) : NULL_STRING;
    }

    public static byte[] pack(int value){
        return Ints.toByteArray(value);
    }

    public static byte[] pack(Integer value){
        return value != null ? pack(value.intValue()) : EMPTY_BYTE_ARRAY;
    }

    public static byte[] pack(long value){
        return Longs.toByteArray(value);
    }

    public static void pack(long value, byte[] dst, int offset) {
        for (int i = 7 + offset; i >= offset; --i) {
            dst[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
    }

    public static int pack(long[] values, byte[] dst, int offset) {
        for (long value : values) {
            TypeConvert.pack(value, dst, offset);
            offset += Long.BYTES;
        }
        return offset;
    }

    public static int pack(int[] values, byte[] dst, int offset) {
        for (int value : values) {
            TypeConvert.pack(value, dst, offset);
            offset += Long.BYTES;
        }
        return offset;
    }

    public static byte[] pack(Long value){
        return value != null ? pack(value.longValue()) : EMPTY_BYTE_ARRAY;
    }

    public static byte[] pack(boolean value){
        return new byte[] { value ? (byte)1 : (byte)0 };
    }

    public static byte[] pack(Boolean value){
        return value != null ? pack(value.booleanValue()) : EMPTY_BYTE_ARRAY;
    }

    public static byte[] pack(double value) {
        return pack(Double.doubleToRawLongBits(value));
    }

    public static byte[] pack(Double value){
        return value != null ? pack(value.doubleValue()) : EMPTY_BYTE_ARRAY;
    }

    public static byte[] pack(Instant value) {
        return value != null ? pack(InstantUtils.toLong(value)) : EMPTY_BYTE_ARRAY;
    }

    public static byte[] pack(LocalDateTime value) {
        return value != null ? pack(LocalDateTimeUtils.toLong(value)) : EMPTY_BYTE_ARRAY;
    }

    public static byte[] packCRC32(String value) {
        byte[] bytes = TypeConvert.pack(value);
        CRC32 checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return TypeConvert.pack(UnsignedInteger.valueOf(checksum.getValue()).intValue());
    }

    public static byte[] unpackBytes(byte[] value) {
        if (!ByteUtils.isNullOrEmpty(value) && value[0] == NULL_BYTE_ARRAY_SCHIELD) {
            value = value.length != 1 ? Arrays.copyOfRange(value, 1, value.length) : null;
        }
        return value;
    }

    public static byte[] pack(byte[] value) {
        if (value == null) {
            value = new byte[]{NULL_BYTE_ARRAY_SCHIELD};
        } else if (value.length != 0 && value[0] == NULL_BYTE_ARRAY_SCHIELD) {
            byte[] newValue = new byte[value.length + 1];
            newValue[0] = NULL_BYTE_ARRAY_SCHIELD;
            System.arraycopy(value, 0, newValue, 1, value.length);
            value = newValue;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T unpack(Class<T> type, byte[] value, TypeConverter<T> packer) {
        if (packer != null) {
            return packer.unpack(value);
        } else if (type == String.class) {
            return (T) unpackString(value);
        } else if (type == Long.class) {
            return (T) unpackLong(value);
        } else if (type == Boolean.class) {
            return (T) unpackBoolean(value);
        } else if (type == Instant.class) {
            return (T) unpackInstant(value);
        } else if (type == Integer.class) {
            return (T) unpackInteger(value);
        } else if (type == Double.class) {
            return (T) unpackDouble(value);
        } else if (type == LocalDateTime.class) {
            return (T) unpackLocalDateTime(value);
        } else if (type == byte[].class) {
            return (T) unpackBytes(value);
        }
        throw new UnsupportedTypeException(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] pack(Class<T> type, Object value, TypeConverter<T> converter){
        if (converter != null) {
            return converter.pack((T) value);
        } else if (type == String.class) {
            return pack((String) value);
        } else if (type == Long.class) {
            return pack((Long) value);
        } else if (type == Boolean.class) {
            return pack((Boolean) value);
        } else if (type == Instant.class) {
            return pack((Instant) value);
        } else if (type == Integer.class) {
            return pack((Integer) value);
        } else if (type == Double.class) {
            return pack((Double) value);
        } else if (type == LocalDateTime.class) {
            return pack((LocalDateTime) value);
        } else if (type == byte[].class) {
            return pack((byte[]) value);
        }
        throw new UnsupportedTypeException(type);
    }
}
