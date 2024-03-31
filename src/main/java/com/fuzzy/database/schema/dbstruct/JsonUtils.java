package com.fuzzy.database.schema.dbstruct;

import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.dbstruct.DBObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

class JsonUtils {

    @FunctionalInterface
    interface ObjectConverter<T> {

        T convert(JSONObject value) throws SchemaException;
    }

    static <T> T parse(String source, Class<T> type) throws SchemaException {
        try {
            Object tablesJson = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(source);
            return castTo(type, tablesJson);
        } catch (ParseException e) {
            throw new SchemaException(e);
        }
    }

    static <T> T getValue(String key, Class<T> type, JSONObject source) throws SchemaException {
        Object val = source.get(key);
        if (val == null) {
            throw new SchemaException("Value of '" + key + "' is null");
        }
        return castTo(type, val);
    }

    static <T> T getValueOrDefault(String key, Class<T> type, JSONObject source, T defaultValue) throws SchemaException {
        Object val = source.get(key);
        return val != null ? castTo(type, val) : defaultValue;
    }

    static int[] getIntArrayValue(String key, JSONObject source) throws SchemaException {
        JSONArray array = getValue(key, JSONArray.class, source);
        int[] value = new int[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            value[i] = castTo(Integer.class, array.get(i));
        }
        return value;
    }

    static <T> List<T> toList(String key, JSONObject source, ObjectConverter<T> converter) throws SchemaException {
        return toList(getValue(key, JSONArray.class, source), converter);
    }

    static <T> List<T> toList(JSONArray source, ObjectConverter<T> converter) throws SchemaException {
        List<T> result = new ArrayList<>(source.size());
        for (Object item : source) {
            result.add(converter.convert(castTo(JSONObject.class, item)));
        }
        return result;
    }

    static <T extends com.fuzzy.database.schema.dbstruct.DBObject> JSONArray toJsonArray(List<T> source) {
        JSONArray result = new JSONArray();
        for (T item : source) {
            result.add(item.toJson());
        }
        return result;
    }

    static <T extends DBObject> JSONArray toJsonArray(int[] source) {
        JSONArray result = new JSONArray();
        for (int item : source) {
            result.add(item);
        }
        return result;
    }

    private static <T> T castTo(Class<T> type, Object value) throws SchemaException {
        if (value.getClass() != type) {
            throw new SchemaException("Unexpected type of value=" + value + ", expected=" + type + ", actual=" + value.getClass());
        }
        return (T) value;
    }
}
