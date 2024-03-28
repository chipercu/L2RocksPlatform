package com.fuzzy.main.rdao.database.exception;

import com.fuzzy.main.rdao.database.schema.BaseIndex;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBIndex;

public class IndexAlreadyExistsException extends SchemaException {

    public <T extends BaseIndex> IndexAlreadyExistsException(T index) {
        super("Index already exists, " + index.toString());
    }

    public <T extends DBIndex> IndexAlreadyExistsException(T index) {
        super("Index already exists, " + index.toString());
    }
}
