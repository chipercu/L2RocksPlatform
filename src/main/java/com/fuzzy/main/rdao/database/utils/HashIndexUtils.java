package com.fuzzy.main.rdao.database.utils;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.Value;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.UnsupportedTypeException;
import com.fuzzy.main.rdao.database.schema.Field;
import com.fuzzy.main.rdao.database.schema.TypeConverter;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBField;
import com.google.common.primitives.UnsignedInts;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class HashIndexUtils {

    public static boolean toLongCastable(Class<?> type) {
        return type != String.class;
    }

    public static void setHashValues(List<Field> fields, Value<Serializable>[] values, long[] destination) {
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            destination[i] = buildHash(field.getType(), values[field.getNumber()].getValue(), field.getConverter());
        }
    }

    public static void setHashValues(final List<Field> sortedFields, final DomainObject object, long[] destination) throws DatabaseException {
        for (int i = 0; i < sortedFields.size(); ++i) {
            Field field = sortedFields.get(i);
            destination[i] = buildHash(field.getType(), object.get(field.getNumber()), field.getConverter());
        }
    }

    public static void setHashValues(final DBField[] sortedFields, final DomainObject object, long[] destination) throws DatabaseException {
        for (int i = 0; i < sortedFields.length; ++i) {
            DBField field = sortedFields[i];
            destination[i] = buildHash(field.getType(), object.get(field.getId()), null);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> long buildHash(Class<T> type, Object value, TypeConverter<T> converter) {
        if (converter != null) {
            return converter.buildHash((T)value);
        }

        if (value == null) {
            return 0;
        }

        if (type == Long.class) {
            return (Long) value;
        } else if (type == String.class) {
            return hash(TypeConvert.pack(((String) value).toLowerCase()));
        } else if (type == Boolean.class) {
            return ((Boolean) value) ? 1 : 0;
        } else if (type == Instant.class) {
            return InstantUtils.toLong((Instant) value);
        } else if (type == LocalDateTime.class) {
            return LocalDateTimeUtils.toLong((LocalDateTime) value);
        } else if (type == Integer.class) {
            return (Integer) value;
        }
        throw new UnsupportedTypeException(type);
    }

    public static boolean equals(Class<?> clazz, Object left, Object right) {
        if (left == null) {
            return right == null;
        }

        if (clazz == String.class) {
            return ((String)left).equalsIgnoreCase((String)right);
        }

        return left.equals(right);
    }

    /**
     * http://www.azillionmonkeys.com/qed/hash.html
     */
    private static long hash(final byte[] data) {
        if (data == null) {
            return 0;
        }

        int len = data.length;
        int hash = len;
        int tmp, rem;

        rem = len & 3;
        len >>>= 2;

        /* Main loop */
        int pos = 0;
        for (; len > 0; --len) {
            hash += get16bits(data, pos);
            tmp = (get16bits(data, pos + 2) << 11) ^ hash;
            hash = ((hash << 16) ^ tmp);
            hash += (hash >>> 11);
            pos += 4;
        }

        /* Handle end cases */
        switch (rem) {
            case 3:
                hash += get16bits(data, pos);
                hash ^= (hash << 16);
                hash ^= ((data[pos + 2] & 0xff) << 18);
                hash += (hash >>> 11);
                break;
            case 2:
                hash += get16bits(data, pos);
                hash ^= (hash << 11);
                hash += (hash >>> 17);
                break;
            case 1:
                hash += (data[pos] & 0xff);
                hash ^= (hash << 10);
                hash += (hash >>> 1);
                break;
        }

        /* Force "avalanching" of final 127 bits */
        hash ^= (hash << 3);
        hash += (hash >>> 5);
        hash ^= (hash << 4);
        hash += (hash >>> 17);
        hash ^= (hash << 25);
        hash += (hash >>> 6);

        return UnsignedInts.toLong(hash);
    }

    private static int get16bits(byte[] data, int startIndex) {
        return (data[startIndex] & 0xff) + ((data[startIndex + 1] << 8) & 0xffff);
    }
}
