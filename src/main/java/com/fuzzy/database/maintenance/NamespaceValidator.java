package com.fuzzy.database.maintenance;

import com.fuzzy.database.provider.DBProvider;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.InconsistentDatabaseException;
import com.fuzzy.database.schema.Schema;

import java.util.*;

public class NamespaceValidator {

    private final DBProvider dbProvider;

    private final Set<String> namespacePrefixes = new HashSet<>();

    public NamespaceValidator(DBProvider dbProvider) {
        this.dbProvider = dbProvider;
    }

    public NamespaceValidator withNamespace(String namespace) {
        if (!namespacePrefixes.add(namespace + StructEntity.NAMESPACE_SEPARATOR)) {
            throw new RuntimeException("Namespace " + namespace + " already exists.");
        }
        return this;
    }

    public void execute() throws DatabaseException {
        validateUnknownColumnFamilies();
    }

    private void validateUnknownColumnFamilies() throws DatabaseException {
        for (String columnFamily : dbProvider.getColumnFamilies()) {
            if (!columnFamily.equals(Schema.SERVICE_COLUMN_FAMILY) && !contains(columnFamily)) {
                throw new InconsistentDatabaseException("Unknown column family " + columnFamily + " .");
            }
        }
    }

    private boolean contains(String columnFamily) {
        for (String namespacePrefix : namespacePrefixes) {
            if (columnFamily.startsWith(namespacePrefix)) {
                return true;
            }
        }

        return false;
    }
}
