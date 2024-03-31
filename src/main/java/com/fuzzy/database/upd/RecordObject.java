package com.fuzzy.database.upd;

import com.fuzzy.database.Record;
import com.fuzzy.database.upd.FieldValue;

import java.io.Serializable;
import java.util.List;

public class RecordObject extends Record {

    private final List<com.fuzzy.database.upd.FieldValue<? extends Serializable>> values;

    public RecordObject(long id, List<com.fuzzy.database.upd.FieldValue<? extends Serializable>> values) {
        super(id, values.stream().map(com.fuzzy.database.upd.FieldValue::getValue).toArray());
        this.values = values;

        values.add(1, new com.fuzzy.database.upd.FieldValue<>("dsf", 1));

        String s = (String) values.get(1).getValue();
    }

    public List<FieldValue<?>> getRecordValues() {
        return values;
    }
}
