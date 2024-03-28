package com.fuzzy.main.rdao.database.exception;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;

public class FieldAlreadyExistsException extends SchemaException {

    public FieldAlreadyExistsException(String fieldName, String tableName, String namespace) {
        super("Field name=" + fieldName + " already exists into '" + namespace + "." + tableName + "'");
    }

    public FieldAlreadyExistsException(int fieldNumber, Class<? extends DomainObject> objClass) {
        super("Field number=" + fieldNumber + " already exists into " + objClass.getSimpleName());
    }
}
