package com.fuzzy.main.rdao.database.schema;

import com.fuzzy.main.rdao.database.utils.IndexUtils;
import com.fuzzy.main.rdao.database.utils.key.KeyUtils;

import java.util.Collections;
import java.util.List;

public abstract class BaseIndex {

    private static final int FIELDS_HASH_BYTE_SIZE = 4;
    private static final int INDEX_NAME_BYTE_SIZE = 3;
    public static final int ATTENDANT_BYTE_SIZE = INDEX_NAME_BYTE_SIZE + FIELDS_HASH_BYTE_SIZE;

    public final byte[] attendant;
    public final String columnFamily;
    public final List<Field> sortedFields;

    BaseIndex(List<Field> sortedIndexedFields, StructEntity parent) {
        this.sortedFields = Collections.unmodifiableList(sortedIndexedFields);
        this.columnFamily = parent.getIndexColumnFamily();
        this.attendant = new byte[ATTENDANT_BYTE_SIZE];
        KeyUtils.putAttendantBytes(this.attendant, getIndexNameBytes(), IndexUtils.buildFieldsHashCRC32(sortedIndexedFields));
    }

    static List<Field> buildIndexedFields(int[] indexedFields, StructEntity parent) {
        return IndexUtils.buildIndexedFields(indexedFields, parent);
    }

    protected abstract byte[] getIndexNameBytes();

    public String[] getFieldNames() {
        return sortedFields.stream().map(Field::getName).toArray(String[]::new);
    }
}