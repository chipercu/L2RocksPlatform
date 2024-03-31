package com.fuzzy.database.schema;

import com.fuzzy.database.exception.FieldNotFoundException;
import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.HashIndex;
import com.fuzzy.database.schema.IntervalIndex;
import com.fuzzy.database.schema.PrefixIndex;
import com.fuzzy.database.schema.RangeIndex;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.schema.dbstruct.*;
import com.fuzzy.database.schema.table.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

class DBTableUtils {

    static Table buildTable(StructEntity structEntity) {
        return new Table(structEntity.getName(),
                structEntity.getNamespace(),
                Arrays.stream(structEntity.getFields()).map(DBTableUtils::buildField).collect(Collectors.toList()),
                structEntity.getHashIndexes().stream().map(DBTableUtils::buildIndex).collect(Collectors.toList()),
                structEntity.getPrefixIndexes().stream().map(DBTableUtils::buildIndex).collect(Collectors.toList()),
                structEntity.getIntervalIndexes().stream().map(DBTableUtils::buildIndex).collect(Collectors.toList()),
                structEntity.getRangeIndexes().stream().map(DBTableUtils::buildIndex).collect(Collectors.toList()));
    }

    static TField buildField(com.fuzzy.database.schema.Field field) {
        if (field.isForeign()) {
            return new TField(field.getName(), new TableReference(field.getForeignDependency().getName(), field.getForeignDependency().getNamespace()));
        }
        return new TField(field.getName(), field.getType());
    }

    static THashIndex buildIndex(com.fuzzy.database.schema.HashIndex index) {
        return new THashIndex(index.getFieldNames());
    }

    static TPrefixIndex buildIndex(com.fuzzy.database.schema.PrefixIndex index) {
        return new TPrefixIndex(index.getFieldNames());
    }

    static TIntervalIndex buildIndex(IntervalIndex index) {
        if (index.getHashedFields() != null && !index.getHashedFields().isEmpty()) {
            return new TIntervalIndex(index.getIndexedField().getName(), index.getHashedFields().stream().map(com.fuzzy.database.schema.Field::getName).toArray(String[]::new));
        }
        return new TIntervalIndex(index.getIndexedField().getName());
    }

    static TRangeIndex buildIndex(com.fuzzy.database.schema.RangeIndex index) {
        if (index.getHashedFields() != null && !index.getHashedFields().isEmpty()) {
            return new TRangeIndex(index.getBeginIndexedField().getName(),
                    index.getEndIndexedField().getName(),
                    index.getHashedFields().stream().map(com.fuzzy.database.schema.Field::getName).toArray(String[]::new));
        }
        return new TRangeIndex(index.getBeginIndexedField().getName(),
                index.getEndIndexedField().getName());
    }

//    static Table buildTable(DBTable table, DBSchema schema) throws SchemaException {
//        return new Table(
//                table.getName(),
//                table.getFields().stream().map(field -> DBTableUtils.buildField(field, schema)).collect(Collectors.toList()),
//                table.getHashIndexes().stream().map(index -> DBTableUtils.buildIndex(index, table)).collect(Collectors.toList()),
//                table.getPrefixIndexes().stream().map(index -> DBTableUtils.buildIndex(index, table)).collect(Collectors.toList()),
//                table.getIntervalIndexes().stream().map(index -> DBTableUtils.buildIndex(index, table)).collect(Collectors.toList()),
//                table.getRangeIndexes().stream().map(index -> DBTableUtils.buildIndex(index, table)).collect(Collectors.toList())
//        );
//    }

//    static Field buildField(DBField field, DBSchema schema) throws SchemaException {
//        if (field.isForeignKey()) {
//            DBTable foreignTable = schema.getTables().stream()
//                    .filter(table -> table.getId() == field.getForeignTableId())
//                    .findFirst()
//                    .orElseThrow(() -> new TableNotFoundException(field.getForeignTableId()));
//
//            return new Field(field.getName(), foreignTable.getName());
//        }
//        return new Field(field.getName(), field.getType());
//    }
//
//    static HashIndex buildIndex(DBHashIndex index, DBTable table) throws SchemaException {
//        return new HashIndex(toFieldNames(index.getFieldIds(), table));
//    }
//
//    static PrefixIndex buildIndex(DBPrefixIndex index, DBTable table) throws SchemaException {
//        return new PrefixIndex(toFieldNames(index.getFieldIds(), table));
//    }
//
//    static IntervalIndex buildIndex(DBIntervalIndex index, DBTable table) throws SchemaException {
//        return new IntervalIndex(
//                getFieldName(index.getIndexedFieldId(), table),
//                toFieldNames(index.getHashFieldIds(), table)
//        );
//    }
//
//    static RangeIndex buildIndex(DBRangeIndex index, DBTable table) throws SchemaException {
//        return new RangeIndex(
//                getFieldName(index.getBeginFieldId(), table),
//                getFieldName(index.getEndFieldId(), table),
//                toFieldNames(index.getHashFieldIds(), table)
//        );
//    }

    private static String getFieldName(int fieldId, DBTable table) throws SchemaException {
        return table.getSortedFields().stream()
                .filter(field -> field.getId() == fieldId)
                .findFirst()
                .orElseThrow(() -> new FieldNotFoundException(fieldId, table.getName()))
                .getName();
    }

    static DBHashIndex buildIndex(HashIndex index, DBTable table) throws SchemaException {
        return new DBHashIndex(toSortedFieldIds(index.getFieldNames(), table));
    }

    static DBHashIndex buildIndex(THashIndex index, DBTable table) throws SchemaException {
        return new DBHashIndex(toSortedFieldIds(index.getFields(), table));
    }

    static DBPrefixIndex buildIndex(PrefixIndex index, DBTable table) throws SchemaException {
        return new DBPrefixIndex(toSortedFieldIds(index.getFieldNames(), table));
    }

    static DBPrefixIndex buildIndex(TPrefixIndex index, DBTable table) throws SchemaException {
        return new DBPrefixIndex(toSortedFieldIds(index.getFields(), table));
    }

    static DBIntervalIndex buildIndex(IntervalIndex index, DBTable table) throws SchemaException {
        return new DBIntervalIndex(
                table.getField(index.getIndexedField().getName()),
                toSortedFieldIds(index.getHashedFields().stream().map(com.fuzzy.database.schema.Field::getName).toArray(String[]::new), table)
        );
    }

    static DBIntervalIndex buildIndex(TIntervalIndex index, DBTable table) throws SchemaException {
        return new DBIntervalIndex(
                table.getField(index.getIndexedField()),
                toSortedFieldIds(index.getHashedFields(), table)
        );
    }

    static DBRangeIndex buildIndex(RangeIndex index, DBTable table) throws SchemaException {
        return new DBRangeIndex(
                table.getField(index.getBeginIndexedField().getName()),
                table.getField(index.getEndIndexedField().getName()),
                toSortedFieldIds(index.getHashedFields().stream().map(Field::getName).toArray(String[]::new), table)
        );
    }

    static DBRangeIndex buildIndex(TRangeIndex index, DBTable table) throws SchemaException {
        return new DBRangeIndex(
                table.getField(index.getBeginField()),
                table.getField(index.getEndField()),
                toSortedFieldIds(index.getHashedFields(), table)
        );
    }

    private static DBField[] toSortedFieldIds(String[] fieldNames, DBTable table) throws SchemaException {
        DBField[] result = new DBField[fieldNames.length];
        for (int i = 0; i < fieldNames.length; ++i) {
            result[i] = table.getField(fieldNames[i]);
        }
        Arrays.sort(result, Comparator.comparing(DBField::getName));
        return result;
    }

    private static String[] toFieldNames(int[] fieldIds, DBTable table) throws SchemaException {
        return Arrays.stream(fieldIds).mapToObj(value -> getFieldName(value, table)).toArray(String[]::new);
    }
}
