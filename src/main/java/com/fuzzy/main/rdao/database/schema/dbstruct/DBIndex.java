package com.fuzzy.main.rdao.database.schema.dbstruct;

import com.fuzzy.main.rdao.database.utils.IndexUtils;
import com.fuzzy.main.rdao.database.utils.key.KeyUtils;

import java.util.Arrays;

public abstract class DBIndex extends DBObject {

    private static final int FIELDS_HASH_BYTE_SIZE = 4;
    private static final int INDEX_NAME_BYTE_SIZE = 3;
    public static final int ATTENDANT_BYTE_SIZE = INDEX_NAME_BYTE_SIZE + FIELDS_HASH_BYTE_SIZE;

    private final byte[] attendant;
    private final int[] fieldIds;

    DBIndex(int id, DBField[] fields) {
        super(id);
        this.fieldIds = Arrays.stream(fields).mapToInt(DBObject::getId).toArray();
        this.attendant = new byte[ATTENDANT_BYTE_SIZE];
        KeyUtils.putAttendantBytes(this.attendant, getIndexNameBytes(), IndexUtils.buildFieldsHashCRC32(fields));
    }

    public int[] getFieldIds() {
        return fieldIds;
    }

    public boolean fieldContains(int fieldId) {
        return contains(fieldId, getFieldIds());
    }

    public boolean fieldsEquals(DBIndex index) {
        return Arrays.equals(index.fieldIds, fieldIds);
    }

    public byte[] getAttendant() {
        return attendant;
    }

    private static boolean contains(int value, int[] destination) {
        for (int item : destination) {
            if (item == value) {
                return true;
            }
        }
        return false;
    }

    static void checkSorting(DBField[] fields) {
        if (fields.length < 2) {
            return;
        }

        for (int i = 1; i < fields.length; ++i) {
            if (fields[i - 1].getName().compareToIgnoreCase(fields[i].getName()) > 0) {
                throw new IllegalArgumentException("wrong sorting " + fields[i].getName());
            }
        }
    }

    protected abstract byte[] getIndexNameBytes();
}
