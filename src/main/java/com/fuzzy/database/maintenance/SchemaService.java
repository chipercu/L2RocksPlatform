package com.fuzzy.database.maintenance;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.exception.TableNotFoundException;
import com.fuzzy.database.maintenance.ChangeMode;
import com.fuzzy.database.maintenance.DomainService;
import com.fuzzy.database.provider.DBProvider;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.Schema;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.InconsistentDatabaseException;
import com.fuzzy.database.schema.dbstruct.DBTable;

import java.util.*;
import java.util.stream.Collectors;

/*
 Не потоко безопасный класс
 */
public class SchemaService {

    private final DBProvider dbProvider;

    private com.fuzzy.database.maintenance.ChangeMode changeModeMode = com.fuzzy.database.maintenance.ChangeMode.NONE;
    private boolean isValidationMode = false;
    private String namespace;
    private Schema schema;
    private Set<String> ignoringNamespaces = new HashSet<>();

    public SchemaService(DBProvider dbProvider) {
        this.dbProvider = dbProvider;
    }

    public SchemaService setChangeMode(ChangeMode value) {
        this.changeModeMode = value;
        return this;
    }

    public SchemaService setValidationMode(boolean value) {
        this.isValidationMode = value;
        return this;
    }

    public SchemaService setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public SchemaService appendIgnoringNamespace(String namespace) {
        ignoringNamespaces.add(namespace);
        return this;
    }

    public SchemaService setSchema(Schema schema) {
        this.schema = schema;
        return this;
    }

    public void execute() throws DatabaseException {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException();
        }
        schema.checkIntegrity();
        validate();
    }

    private void validate() throws DatabaseException {
        if (isValidationMode) {
//            validateUnknownColumnFamilies(); todo make validation!!!!!!
        }
    }

    private void validateConsistentNames() throws InconsistentDatabaseException {
        final String namespacePrefix = namespace + StructEntity.NAMESPACE_SEPARATOR;
        Set<String> processedNames = new HashSet<>();
        for (DBTable domain : schema.getDbSchema().getTables()) {
            if (processedNames.contains(domain.getDataColumnFamily())) {
                throw new InconsistentDatabaseException("Column family " + domain.getNamespace() + " into " + domain.getName() + " already exists.");
            }

            if (!domain.getDataColumnFamily().startsWith(namespacePrefix)) {
                throw new InconsistentDatabaseException("Namespace " + namespace + " is not consistent with " + domain.getName());
            }

            processedNames.add(domain.getNamespace());
        }

        for (String value : ignoringNamespaces) {
            if (!value.startsWith(namespacePrefix)) {
                throw new InconsistentDatabaseException("Namespace " + namespace + " is not consistent with " + value);
            }
        }
    }

    private void validateUnknownColumnFamilies() throws DatabaseException {
        final String namespacePrefix = namespace + StructEntity.NAMESPACE_SEPARATOR;
        Set<String> columnFamilies = Arrays.stream(dbProvider.getColumnFamilies())
                .filter(s -> s.startsWith(namespacePrefix))
                .collect(Collectors.toSet());
        for (StructEntity domain : schema.getDomains()) {
            DomainService.removeDomainColumnFamiliesFrom(columnFamilies, domain);
        }

        for (String space : ignoringNamespaces) {
            final String spacePrefix = space + StructEntity.NAMESPACE_SEPARATOR;
            columnFamilies.removeIf(s -> s.startsWith(spacePrefix));
        }

        if (!columnFamilies.isEmpty()) {
            throw new InconsistentDatabaseException("Namespace " + namespace + " contains unknown column families " + String.join(", ", columnFamilies) + ".");
        }
    }

    public static void install(Set<Class<? extends DomainObject>> domainClasses, DBProvider dbProvider) throws DatabaseException {
        Schema schema;
        if (Schema.exists(dbProvider)) {
            schema = Schema.read(dbProvider);
        } else {
            schema = Schema.create(dbProvider);
        }
        Set<StructEntity> modifiableDomains = domainClasses.stream().map(SchemaService::getNewEntity).collect(Collectors.toSet());
        if (modifiableDomains.stream().noneMatch(schema::existTable)) {
            createTables(schema, modifiableDomains);
        }
        if (modifiableDomains.stream().anyMatch(d -> !schema.existTable(d))) {
            throw new SchemaException("Inconsistent schema error. Schema doesn't contain table: " + modifiableDomains.stream().filter(d -> !schema.existTable(d))
                    .map(StructEntity::getColumnFamily)
                    .findAny()
                    .orElse(null));
        }
        schema.checkIntegrity();
    }

    private static StructEntity getNewEntity(Class<? extends DomainObject> domain) {
        Class<? extends DomainObject> annotationClass = StructEntity.getAnnotationClass(domain);
        return new StructEntity(annotationClass);
    }

    private static void createTables(Schema schema, Set<StructEntity> modifiableDomains) throws DatabaseException {
        Set<StructEntity> notCreatedTables = new HashSet<>();
        for (StructEntity domain : modifiableDomains) {
            if (isForeignDependenciesCreated(schema, domain)) {
                schema.createTable(domain);
            } else {
                notCreatedTables.add(domain);
            }
        }
        if (notCreatedTables.size() == 0) {
            return;
        }
        if (notCreatedTables.size() == modifiableDomains.size()) {
            throw new TableNotFoundException(notCreatedTables.stream().findFirst().get().getName());
        }
        createTables(schema, notCreatedTables);
    }

    private static boolean isForeignDependenciesCreated(Schema schema, StructEntity domain) {
        for (Field field : domain.getFields()) {
            if (field.isForeign() && field.getForeignDependency() != domain && !schema.existTable(field.getForeignDependency())) {
                return false;
            }
        }
        return true;
    }
}
