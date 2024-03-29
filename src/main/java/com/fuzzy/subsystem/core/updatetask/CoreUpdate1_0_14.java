package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.table.TField;
import com.infomaximum.database.schema.table.THashIndex;
import com.infomaximum.database.schema.table.Table;
import com.infomaximum.database.schema.table.TableReference;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.Query;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeEditable;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeEditable;

import java.util.List;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.14.x",
        previousVersion = "1.0.13.x"
)
public class CoreUpdate1_0_14 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_14(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        createAdditionalFieldTable(schema);
        createAdditionalFieldValueTable(schema);
        fixPrivilegeTablesContent();
    }

    private void createAdditionalFieldTable(Schema schema) {
        TField objectField = new TField("object", String.class);
        TField keyField = new TField("key", String.class);
        List<TField> fields = List.of(
                objectField,
                keyField,
                new TField("name", String.class),
                new TField("data_type", Integer.class),
                new TField("index", Integer.class)
        );
        List<THashIndex> hashIndexes = List.of(
                new THashIndex(objectField),
                new THashIndex(objectField, keyField)
        );
        schema.createTable(new Table("AdditionalField", CoreSubsystemConsts.UUID, fields, hashIndexes));
    }

    private void createAdditionalFieldValueTable(Schema schema) {
        TField additionalFieldIdField = new TField("additional_field_id",
                new TableReference("AdditionalField", CoreSubsystemConsts.UUID));
        TField objectIdField = new TField("object_id", Long.class);
        TField indexField = new TField("index", Integer.class);
        TField stringValueField = new TField("string_value", String.class);
        TField longValueField = new TField("long_value", Long.class);
        List<TField> fields = List.of(
                additionalFieldIdField,
                objectIdField,
                indexField,
                stringValueField,
                longValueField
        );
        List<THashIndex> hashIndexes = List.of(
                new THashIndex(additionalFieldIdField),
                new THashIndex(additionalFieldIdField, objectIdField),
                new THashIndex(additionalFieldIdField, indexField),
                new THashIndex(additionalFieldIdField, objectIdField, indexField),
                new THashIndex(additionalFieldIdField, longValueField, indexField),
                new THashIndex(additionalFieldIdField, stringValueField, indexField)
        );
        schema.createTable(new Table("AdditionalFieldValue", CoreSubsystemConsts.UUID, fields, hashIndexes));
    }

    private void fixPrivilegeTablesContent() {
        executeQuery(new Query<Void>() {

            private RemovableResource<AccessRoleCorePrivilegeEditable> accessRoleCorePrivilegeRemovableResource;
            private RemovableResource<ApiKeyCorePrivilegeEditable> apiKeyCorePrivilegeRemovableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleCorePrivilegeRemovableResource =
                        resources.getRemovableResource(AccessRoleCorePrivilegeEditable.class);
                apiKeyCorePrivilegeRemovableResource = resources.getRemovableResource(ApiKeyCorePrivilegeEditable.class);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                accessRoleCorePrivilegeRemovableResource.forEach(accessRoleCorePrivilege -> {
                    if (accessRoleCorePrivilege.getPrivilege() == null) {
                        accessRoleCorePrivilegeRemovableResource.remove(accessRoleCorePrivilege, transaction);
                    }
                }, transaction);
                apiKeyCorePrivilegeRemovableResource.forEach(apiKeyCorePrivilege -> {
                    if (apiKeyCorePrivilege.getPrivilege() == null) {
                        apiKeyCorePrivilegeRemovableResource.remove(apiKeyCorePrivilege, transaction);
                    }
                }, transaction);
                return null;
            }
        });
    }
}