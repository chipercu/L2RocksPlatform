package com.fuzzy.database.utils;

import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBHashIndex;
import com.fuzzy.database.schema.dbstruct.DBSchema;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.schema.table.FieldReference;

import java.util.*;
import java.util.stream.Collectors;

public class SchemaTableCache {

    private final DBSchema schema;
    private final Map<String, DBTable> uuidTableMap;
    private final Map<String, Set<FieldReference>> uuidForeignTableFieldReferences;


    public SchemaTableCache(List<DBTable> tables, DBSchema schema) {
        this.schema = schema;
        this.uuidTableMap = tables.stream()
                .collect(Collectors.toMap(table -> buildUuidTable(table.getName(), table.getNamespace()),
                        table -> table));
        this.uuidForeignTableFieldReferences = new HashMap<>();
        putFieldReferences(tables);
    }

    public DBTable getTable(String tableName, String namespace) {
        return uuidTableMap.get(buildUuidTable(tableName, namespace));
    }

    public void newTable(DBTable table) {
        uuidTableMap.put(buildUuidTable(table.getName(), table.getNamespace()), table);
        putFieldReferences(table);
    }

    public void createField(DBField field, DBTable table) {
        if (field.isForeignKey()) {
            putFieldReference(field, table);
        }
    }

    public void removeField(DBField field, DBTable table) {
        if (field.isForeignKey()) {
            DBTable referencedTable = schema.getTableById(field.getForeignTableId());
            uuidForeignTableFieldReferences.get(buildUuidTable(referencedTable.getName(), referencedTable.getNamespace()))
                    .remove(new FieldReference(table.getName(), table.getNamespace(), null));
        }
    }

    public void removeTable(String name, String namespace) {
        uuidTableMap.remove(buildUuidTable(name, namespace));
        uuidForeignTableFieldReferences.remove(buildUuidTable(name, namespace));
    }

    public Set<FieldReference> getTableReference(String name, String namespace) {
        return uuidForeignTableFieldReferences.getOrDefault(buildUuidTable(name, namespace), Collections.emptySet());
    }

    private String buildUuidTable(String tableName, String namespace) {
        return tableName + "." + namespace;
    }

    private void putFieldReferences(DBTable table) {
        for (DBField field : table.getSortedFields()) {
            if (field.isForeignKey()) {
                putFieldReference(field, table);
            }
        }
    }

    private void putFieldReferences(List<DBTable> tables) {
        tables.forEach(this::putFieldReferences);
    }

    private void putFieldReference(DBField field, DBTable table) {
        DBTable referencedTable = schema.getTableById(field.getForeignTableId());
        FieldReference fieldReference = new FieldReference(table.getName(), table.getNamespace(), new DBHashIndex(field));
        String tableUuid = buildUuidTable(referencedTable.getName(), referencedTable.getNamespace());
        Set<FieldReference> references = uuidForeignTableFieldReferences.get(tableUuid);
        if (references == null) {
            references = new HashSet<>();
            references.add(fieldReference);
            uuidForeignTableFieldReferences.put(tableUuid, references);
        } else {
            references.add(fieldReference);
        }
    }
}
