package com.fuzzy.main.rdao.database.exception;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBField;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;

import java.util.Set;
import java.util.stream.Collectors;

public class IndexNotFoundException extends DatabaseException {

    public IndexNotFoundException(String indexName, Class<? extends DomainObject> domainClass) {
        super("Not found " + indexName + " in " + domainClass);
    }

    public IndexNotFoundException(Set<Integer> indexedFieldIds, DBTable table) {
        super("Not found " +
                indexedFieldIds.stream()
                        .map(table::getField)
                        .map(DBField::getName)
                        .collect(Collectors.joining(", ")) +
                " in " + table.getNamespace() + "." + table.getName());
    }
}

