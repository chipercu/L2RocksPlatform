package com.fuzzy.main.rdao.database.schema.dbstruct;


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
