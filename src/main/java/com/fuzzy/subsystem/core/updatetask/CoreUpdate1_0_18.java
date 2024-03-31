package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.database.domainobject.Transaction;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.schema.Schema;
import com.fuzzy.database.schema.table.THashIndex;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.update.UpdateTask;
import com.fuzzy.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeEditable;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeEditable;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.18.x",
        previousVersion = "1.0.17.x"
)
//    -upgrade {"update":[
//    {"uuid":"com.infomaximum.subsystem.core","old_version":"1.0.17.0","new_version":"1.0.18.0"},
//    {"uuid":"com.infomaximum.subsystem.frontend","old_version":"1.0.17.0","new_version":"1.0.18.0"}
//    ]}
public class CoreUpdate1_0_18 extends UpdateTask<CoreSubsystem> {
    private final String employeeTableName = "Employee";

    public CoreUpdate1_0_18(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        fixPrivilegeTablesContent();
        Schema schema = getSchema(transaction);
        updateEmployeeTable(schema);
    }

    private void fixPrivilegeTablesContent() {
        executeQuery(new Query<Void>() {

            private RemovableResource<AccessRoleCorePrivilegeEditable> accessRoleCorePrivilegeRemovableResource;
            private RemovableResource<ApiKeyCorePrivilegeEditable> apiKeyCorePrivilegeRemovableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleCorePrivilegeRemovableResource = resources.getRemovableResource(AccessRoleCorePrivilegeEditable.class);
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

    private void updateEmployeeTable(Schema schema) {
        schema.dropIndex(new THashIndex("notifications_of_disabled_logon"), employeeTableName, CoreSubsystemConsts.UUID);
        schema.dropField("notifications_of_disabled_logon", employeeTableName, CoreSubsystemConsts.UUID);
    }
}