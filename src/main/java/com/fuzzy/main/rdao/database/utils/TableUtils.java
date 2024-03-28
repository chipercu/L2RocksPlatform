package com.fuzzy.main.rdao.database.utils;

import com.fuzzy.main.rdao.database.schema.dbstruct.*;
import com.fuzzy.main.rdao.database.schema.table.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TableUtils {

    public static Table buildTable(DBTable dbTable, DBSchema schema) {
        return new Table(dbTable.getName(),
                dbTable.getNamespace(),
                dbTable.getSortedFields().stream().map(field -> buildField(field, schema)).collect(Collectors.toList()),
                dbTable.getHashIndexes().stream().map(index -> buildIndex(index, dbTable)).collect(Collectors.toList()),
                dbTable.getPrefixIndexes().stream().map(index -> buildIndex(index, dbTable)).collect(Collectors.toList()),
                dbTable.getIntervalIndexes().stream().map(index -> buildIndex(index, dbTable)).collect(Collectors.toList()),
                dbTable.getRangeIndexes().stream().map(index -> buildIndex(index, dbTable)).collect(Collectors.toList())
        );

    }

    public static TField buildField(DBField dbField, DBSchema schema) {
        if (dbField.isForeignKey()) {
            DBTable foreignTable = schema.getTableById(dbField.getForeignTableId());
            return new TField(dbField.getName(), new TableReference(foreignTable.getName(), foreignTable.getNamespace()));
        }
        return new TField(dbField.getName(), dbField.getType());
    }

    public static THashIndex buildIndex(DBHashIndex dbIndex, DBTable dbTable) {
        return new THashIndex(Arrays
                .stream(dbIndex.getFieldIds())
                .mapToObj(id -> dbTable.getField(id).getName())
                .toArray(String[]::new)
        );
    }

    public static TPrefixIndex buildIndex(DBPrefixIndex dbIndex, DBTable dbTable) {
        return new TPrefixIndex(Arrays
                .stream(dbIndex.getFieldIds())
                .mapToObj(id -> dbTable.getField(id).getName())
                .toArray(String[]::new)
        );
    }

    public static TIntervalIndex buildIndex(DBIntervalIndex dbIndex, DBTable dbTable) {
        String indexedField = dbTable.getField(dbIndex.getIndexedFieldId()).getName();
        String[] hashFields = Arrays
                .stream(dbIndex.getHashFieldIds())
                .mapToObj(id -> dbTable.getField(id).getName())
                .toArray(String[]::new);
        return new TIntervalIndex(indexedField, hashFields);
    }

    public static TRangeIndex buildIndex(DBRangeIndex dbIndex, DBTable dbTable) {
        String beginField = dbTable.getField(dbIndex.getBeginFieldId()).getName();
        String endField = dbTable.getField(dbIndex.getEndFieldId()).getName();
        String[] hashFields = Arrays
                .stream(dbIndex.getHashFieldIds())
                .mapToObj(id -> dbTable.getField(id).getName())
                .toArray(String[]::new);
        return new TRangeIndex(beginField, endField, hashFields);
    }

    public static Object[] sortValuesByFieldOrder(String tableName, String namespace, String[] fields, Object[] values, DBSchema schema) {
        DBTable table = schema.getTable(tableName, namespace);
        Object[] result = new Object[table.getSortedFields().size()];
        for (int i = 0; i < fields.length; i++) {
            int fieldId = table.getFieldIndex(fields[i]);
            result[fieldId] = values[i];
        }
        return result;
    }

    public static Object[] sortValuesByFieldOrder(String tableName, String namespace, String[] fields, Object[] values, Object[] prevValues, DBSchema schema) {
        DBTable table = schema.getTable(tableName, namespace);
        Object[] result = Arrays.copyOf(prevValues, prevValues.length);
        for (int i = 0; i < fields.length; i++) {
            int fieldId = table.getFieldIndex(fields[i]);
            result[fieldId] = values[i];
        }
        return result;
    }
}
