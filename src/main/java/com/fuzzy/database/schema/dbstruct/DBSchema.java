package com.fuzzy.database.schema.dbstruct;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.FieldAlreadyExistsException;
import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.exception.TableNotFoundException;
import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBObject;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.schema.dbstruct.JsonUtils;
import com.fuzzy.database.schema.table.FieldReference;
import com.fuzzy.database.schema.table.TableReference;
import com.fuzzy.database.utils.SchemaTableCache;
import net.minidev.json.JSONArray;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBSchema {

    private final String version;
    private final List<com.fuzzy.database.schema.dbstruct.DBTable> tables;

    private final SchemaTableCache schemaTableCache;

    private DBSchema(String version, List<com.fuzzy.database.schema.dbstruct.DBTable> tables) {
        this.version = version;
        this.tables = tables;
        this.schemaTableCache = new SchemaTableCache(tables, this);
    }

    public String getVersion() {
        return version;
    }

    public List<com.fuzzy.database.schema.dbstruct.DBTable> getTables() {
        return tables;
    }

    public com.fuzzy.database.schema.dbstruct.DBTable newTable(String name, String namespace, List<com.fuzzy.database.schema.dbstruct.DBField> columns) {
        com.fuzzy.database.schema.dbstruct.DBTable dbTable = new com.fuzzy.database.schema.dbstruct.DBTable(nextId(tables), name, namespace, columns);
        tables.add(dbTable);
        schemaTableCache.newTable(dbTable);
        return dbTable;
    }

    public void dropTable(String name, String namespace) {
        schemaTableCache.removeTable(name, namespace);
    }

    public int findTableIndex(String tableName, String tableNamespace) throws SchemaException {
        com.fuzzy.database.schema.dbstruct.DBTable dbTable;
        for (int i = 0; i < tables.size(); ++i) {
            dbTable = tables.get(i);
            if (dbTable.getName().equals(tableName) && dbTable.getNamespace().equals(tableNamespace)) {
                return i;
            }
        }
        return -1;
    }

    public com.fuzzy.database.schema.dbstruct.DBTable getTableById(int id) throws SchemaException {
        return tables.stream().filter(table ->  table.getId() == id)
                .findAny()
                .orElseThrow(() -> new TableNotFoundException("Table with id: " + id + " doesn't found"));
    }

    public List<com.fuzzy.database.schema.dbstruct.DBTable> getTablesByNamespace(String namespace) throws SchemaException {
        return tables.stream().filter(table ->  table.getNamespace().equals(namespace)).collect(Collectors.toList());
    }

    public com.fuzzy.database.schema.dbstruct.DBTable getTable(String name, String namespace) throws SchemaException {
        com.fuzzy.database.schema.dbstruct.DBTable table = schemaTableCache.getTable(name, namespace);
        if (table == null) {
            throw new TableNotFoundException(namespace + "." + name);
        }
        return table;
    }

    public com.fuzzy.database.schema.dbstruct.DBField createField(String fieldName,
                                                                        Class<? extends Serializable> fieldType,
                                                                        TableReference fieldForeignTable,
                                                                        String tableName,
                                                                        String tableNamespace) throws DatabaseException {
        com.fuzzy.database.schema.dbstruct.DBTable dbTable = getTable(tableName, tableNamespace);
        int i = dbTable.findFieldIndex(fieldName);
        if (i != -1) {
            throw new FieldAlreadyExistsException(fieldName, dbTable.getName(), dbTable.getNamespace());
        }

        Integer fTableId = fieldForeignTable != null
                ? getTable(fieldForeignTable.getName(), fieldForeignTable.getNamespace()).getId()
                : null;
        com.fuzzy.database.schema.dbstruct.DBField dbField = dbTable.newField(fieldName, fieldType, fTableId);
        schemaTableCache.createField(dbField, dbTable);
        return dbField;
    }

    public void dropField(String fieldName, String tableName, String namespace) throws DatabaseException {
        com.fuzzy.database.schema.dbstruct.DBTable table = getTable(tableName, namespace);
        DBField field = table.getField(fieldName);
        schemaTableCache.removeField(field, table);
    }

    public void checkIntegrity() throws SchemaException {
        checkUniqueId(tables);

        for (com.fuzzy.database.schema.dbstruct.DBTable table : tables) {
            table.checkIntegrity();
        }
    }

    public static DBSchema fromStrings(String version, String tablesJson) throws SchemaException {
        return new DBSchema(version, JsonUtils.toList(JsonUtils.parse(tablesJson, JSONArray.class), DBTable::fromJson));
    }

    public String toTablesJsonString() {
        return JsonUtils.toJsonArray(tables).toJSONString();
    }

    public Set<FieldReference> getTableReferences(String tableName, String namespace) {
        return schemaTableCache.getTableReference(tableName, namespace);
    }

    static int nextId(List<? extends com.fuzzy.database.schema.dbstruct.DBObject> items) {
        return nextId(items.stream());
    }

    static int nextId(Stream<? extends com.fuzzy.database.schema.dbstruct.DBObject> items) {
        return items
                .map(com.fuzzy.database.schema.dbstruct.DBObject::getId)
                .max(Integer::compare)
                .orElse(-1) + 1;
    }

    static <T extends DBObject> void checkUniqueId(List<T> objects) throws SchemaException {
        for (int i = 0; i < objects.size(); ++i) {
            T obj = objects.get(i);

            for (int j = i + 1; j < objects.size(); ++j) {
                if (objects.get(j).getId() == obj.getId()) {
                    throw new SchemaException("Not unique id=" + obj.getId() + " for " + obj.getClass().getSimpleName());
                }
            }
        }
    }
}
