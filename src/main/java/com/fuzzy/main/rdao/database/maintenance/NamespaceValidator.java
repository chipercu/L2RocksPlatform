package com.fuzzy.main.rdao.database.maintenance;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.InconsistentDatabaseException;
import com.fuzzy.main.rdao.database.provider.DBProvider;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;

import java.util.HashSet;
import java.util.Set;

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
