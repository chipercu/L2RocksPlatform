package com.fuzzy.database.schema.dbstruct;


import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBIndex;
import com.fuzzy.database.schema.dbstruct.DBTable;

public abstract class DBBaseIntervalIndex extends DBIndex {

    private final int[] hashFieldIds;

    DBBaseIntervalIndex(int id, DBField[] fields, int[] hashFieldIds) {
        super(id, fields);
        this.hashFieldIds = hashFieldIds;
    }

    public int[] getHashFieldIds() {
        return hashFieldIds;
    }

    public abstract Class<?> checkIndexedFieldType(Class<?> expectedType, DBTable table);
}
