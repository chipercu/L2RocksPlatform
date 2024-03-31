package com.fuzzy.database.schema.dbstruct;

import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBIndex;
import com.fuzzy.database.schema.dbstruct.JsonUtils;
import com.fuzzy.database.utils.IndexUtils;
import com.fuzzy.database.utils.TypeConvert;
import net.minidev.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DBHashIndex extends DBIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("hsh");
    private static final String JSON_PROP_FIELD_IDS = "field_ids";

    DBHashIndex(int id, com.fuzzy.database.schema.dbstruct.DBField... fields) {
        super(id, fields);
        checkSorting(fields);
    }

    public DBHashIndex(com.fuzzy.database.schema.dbstruct.DBField... fields) {
        this(-1, fields);
    }

    @Override
    protected byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }

    static DBHashIndex fromJson(JSONObject source, List<com.fuzzy.database.schema.dbstruct.DBField> tableFields) throws SchemaException {
        com.fuzzy.database.schema.dbstruct.DBField[] fields = IndexUtils.getFieldsByIds(tableFields, com.fuzzy.database.schema.dbstruct.JsonUtils.getIntArrayValue(JSON_PROP_FIELD_IDS, source));
        Arrays.sort(fields, Comparator.comparing(DBField::getName));
        return new DBHashIndex(
                com.fuzzy.database.schema.dbstruct.JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                fields
        );
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_FIELD_IDS, com.fuzzy.database.schema.dbstruct.JsonUtils.toJsonArray(getFieldIds()));
        return object;
    }
}
