package com.fuzzy.main.rdao.database.utils.key;

import com.fuzzy.main.rdao.database.exception.KeyCorruptedException;
import com.fuzzy.main.rdao.database.schema.BaseIndex;

public class KeyUtils {

    /**
     * Заполняет начало байтового массива названием индекса и хэшем индексируемых полей: [index_name][fields_hash]...
     */
    public static void putAttendantBytes(byte[] destination, final byte[] indexName, final byte[] fieldsHash) {
        if (destination.length < BaseIndex.ATTENDANT_BYTE_SIZE) {
            throw new IllegalArgumentException("Attendant size more than buffer size");
        }
        System.arraycopy(indexName, 0, destination, 0, indexName.length);
        System.arraycopy(fieldsHash, 0, destination, indexName.length, fieldsHash.length);
    }

    static byte[] allocateAndPutIndexAttendant(int size, byte[] attendant) {
        if (size < attendant.length) {
            throw new IllegalArgumentException("Attendant size more than buffer size");
        }
        byte[] result = new byte[size];
        System.arraycopy(attendant, 0, result, 0, attendant.length);
        return result;
    }

    public static byte[] getIndexAttendant(byte[] src) {
        if (src.length < BaseIndex.ATTENDANT_BYTE_SIZE) {
            throw new KeyCorruptedException(src);
        }
        byte[] result = new byte[BaseIndex.ATTENDANT_BYTE_SIZE];
        System.arraycopy(src, 0, result, 0, BaseIndex.ATTENDANT_BYTE_SIZE);
        return result;
    }
}