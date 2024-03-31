package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.BaseIndex;
import com.fuzzy.database.schema.dbstruct.DBIndex;

public class IndexAlreadyExistsException extends SchemaException {

    public <T extends BaseIndex> IndexAlreadyExistsException(T index) {
        super("Index already exists, " + index.toString());
    }

    public <T extends DBIndex> IndexAlreadyExistsException(T index) {
        super("Index already exists, " + index.toString());
    }
}
