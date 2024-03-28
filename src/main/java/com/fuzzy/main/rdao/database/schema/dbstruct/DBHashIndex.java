package com.fuzzy.main.rdao.database.schema.dbstruct;

import com.fuzzy.main.rdao.database.exception.SchemaException;
import com.fuzzy.main.rdao.database.utils.IndexUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import net.minidev.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DBHashIndex extends DBIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("hsh");
    private static final String JSON_PROP_FIELD_IDS = "field_ids";

    DBHashIndex(int id, DBField... fields) {
        super(id, fields);
        checkSorting(fields);
    }

    public DBHashIndex(DBField... fields) {
        this(-1, fields);
    }

    @Override
    protected byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }

    static DBHashIndex fromJson(JSONObject source, List<DBField> tableFields) throws SchemaException {
        DBField[] fields = IndexUtils.getFieldsByIds(tableFields, JsonUtils.getIntArrayValue(JSON_PROP_FIELD_IDS, source));
        Arrays.sort(fields, Comparator.comparing(DBField::getName));
        return new DBHashIndex(
                JsonUtils.getValue(JSON_PROP_ID, Integer.class, source),
                fields
        );
    }

    @Override
    JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(JSON_PROP_ID, getId());
        object.put(JSON_PROP_FIELD_IDS, JsonUtils.toJsonArray(getFieldIds()));
        return object;
    }
}
