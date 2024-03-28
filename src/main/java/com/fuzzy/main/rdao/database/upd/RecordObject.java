package com.fuzzy.main.rdao.database.upd;

import com.fuzzy.main.rdao.database.Record;

import java.io.Serializable;
import java.util.List;

public class RecordObject extends Record {

    private final List<FieldValue<? extends Serializable>> values;

    public RecordObject(long id, List<FieldValue<? extends Serializable>> values) {
        super(id, values.stream().map(FieldValue::getValue).toArray());
        this.values = values;

        values.add(1, new FieldValue<>("dsf", 1));

        String s = (String) values.get(1).getValue();
    }

    public List<FieldValue<?>> getRecordValues() {
        return values;
    }
}
