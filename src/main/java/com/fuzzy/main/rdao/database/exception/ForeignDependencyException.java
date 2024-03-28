package com.fuzzy.main.rdao.database.exception;

import com.fuzzy.main.rdao.database.schema.Field;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBField;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;

public class ForeignDependencyException extends DatabaseException {

    public ForeignDependencyException(long objId, Class objClass, Field foreignField, long notExistenceFieldValue) {
        super(String.format("Foreign field %s.%s = %d not exists into %s, for object id = %d.",
                objClass.getName(), foreignField.getName(), notExistenceFieldValue,
                foreignField.getForeignDependency().getObjectClass().getName(), objId));
    }

    public ForeignDependencyException(long recordId,
                                      String table,
                                      String tableNamespace,
                                      String foreignTable,
                                      String foreignTableNamespace,
                                      DBField foreignField,
                                      long notExistenceFieldValue) {
        super(String.format("Foreign field %s.%s.%s = %d not exists into %s.%s, for object id = %d.",
                table,
                tableNamespace,
                foreignField.getName(),
                notExistenceFieldValue,
                foreignTable,
                foreignTableNamespace,
                recordId));
    }

    public ForeignDependencyException(long recordId,
                                      DBTable table,
                                      DBTable foreignTable,
                                      DBField foreignField,
                                      long notExistenceFieldValue) {
        this(recordId, table.getName(), table.getNamespace(), foreignTable.getName(), foreignTable.getNamespace(), foreignField, notExistenceFieldValue);
    }

    public ForeignDependencyException(long removingId, Class removingClass, long referencingId, Class referencingClass) {
        super(String.format("Object %s.id = %d referenced to removing %s.id = %d.",
                referencingClass.getName(), referencingId,
                removingClass.getName(), removingId));
    }

    public ForeignDependencyException(long removingId,
                                      String removingTableName,
                                      String removingNamespace,
                                      long referencingId,
                                      String referencingTableName,
                                      String referencingNamespace) {
        super(String.format("Object %s.%s.id = %d referenced to removing %s.%s.id = %d.",
                referencingNamespace, referencingTableName, referencingId,
                removingNamespace, removingTableName, removingId));
    }
}
