package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.table.TField;
import com.infomaximum.database.schema.table.THashIndex;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.Query;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.function.Consumer;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeEditable;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeReadable;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeEditable;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeReadable;
import com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationReadable;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.20.x",
        previousVersion = "1.0.19.x"
)
public class CoreUpdate1_0_20 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_20(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        updateEmployeeTable(schema);
        removeMailServerPrivileges();
        actualizationEmployeeSystemNotification(schema);
    }

    private void actualizationEmployeeSystemNotification(Schema schema) {
        Entity entity = EmployeeSystemNotificationReadable.class.getAnnotation(Entity.class);
        schema.dropField("employee_id", entity.name(), entity.namespace());
    }

    private void updateEmployeeTable(Schema schema) {
        String employeeTableName = "Employee";
        final TField sendSystemEventsField = new TField("send_system_events", Boolean.class);
        schema.createField(sendSystemEventsField, employeeTableName, CoreSubsystemConsts.UUID);
        schema.createIndex(new THashIndex(sendSystemEventsField), employeeTableName, CoreSubsystemConsts.UUID);
    }


    private void removeMailServerPrivileges() {
        executeQuery(new Query<Void>() {
            private RemovableResource<AccessRoleCorePrivilegeEditable> accessRoleCorePrivilegeResource;
            private RemovableResource<ApiKeyCorePrivilegeEditable> apiKeyCorePrivilegeResource;
            private final Integer mailServerKey = 2;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleCorePrivilegeResource = resources.getRemovableResource(AccessRoleCorePrivilegeEditable.class);
                apiKeyCorePrivilegeResource = resources.getRemovableResource(ApiKeyCorePrivilegeEditable.class);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                Consumer<AccessRoleCorePrivilegeEditable> accessRoleRemover = o -> {
                    Integer privilegeUniqueKey = o.get(AccessRoleCorePrivilegeReadable.FIELD_PRIVILEGE);
                    if (privilegeUniqueKey.equals(mailServerKey)) {
                        accessRoleCorePrivilegeResource.remove(o, transaction);
                    }
                };
                Consumer<ApiKeyCorePrivilegeEditable> apiKeyRemover = o -> {
                    Integer apiKeyUniqueKey = o.get(ApiKeyCorePrivilegeReadable.FIELD_PRIVILEGE);
                    if (apiKeyUniqueKey.equals(mailServerKey)) {
                        apiKeyCorePrivilegeResource.remove(o, transaction);
                    }
                };
                accessRoleCorePrivilegeResource.forEach(accessRoleRemover, transaction);
                apiKeyCorePrivilegeResource.forEach(apiKeyRemover, transaction);
                return null;
            }
        });
    }
}