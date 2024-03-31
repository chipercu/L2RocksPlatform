package com.fuzzy.database.exception;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.exception.SchemaException;

public class FieldAlreadyExistsException extends SchemaException {

    public FieldAlreadyExistsException(String fieldName, String tableName, String namespace) {
        super("Field name=" + fieldName + " already exists into '" + namespace + "." + tableName + "'");
    }

    public FieldAlreadyExistsException(int fieldNumber, Class<? extends DomainObject> objClass) {
        super("Field number=" + fieldNumber + " already exists into " + objClass.getSimpleName());
    }
}
