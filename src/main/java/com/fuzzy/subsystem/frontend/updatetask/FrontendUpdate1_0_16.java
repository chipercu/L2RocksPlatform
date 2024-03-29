package com.fuzzy.subsystem.frontend.updatetask;

import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.table.TField;
import com.fuzzy.main.rdao.database.schema.table.THashIndex;
import com.fuzzy.main.rdao.database.schema.table.Table;
import com.fuzzy.main.rdao.database.schema.table.TableReference;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.*;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.main.platform.update.exception.UpdateException;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.FrontendSubsystemConsts;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege.AccessRoleFrontendPrivilegeEditable;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.Arrays;
import java.util.Collections;

@Update(
        componentUUID = FrontendSubsystemConsts.UUID,
        version = "1.0.16.x",
        previousVersion = "1.0.15.x"
)
public class FrontendUpdate1_0_16 extends UpdateTask<FrontendSubsystem> {
    /*
    -upgrade {"update":[{"uuid":"com.infomaximum.subsystem.frontend","old_version":"1.0.15","new_version":"1.0.16"}]}
    */

    public FrontendUpdate1_0_16(FrontendSubsystem component) {
        super(component);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        try {
            Schema schema = getSchema(transaction);
            createAccessRoleFrontendPrivilege(schema);
            updateAdministratorAccessRole();
        } catch (Throwable e) {
            throw new UpdateException(e);
        }
    }

    private void createAccessRoleFrontendPrivilege(Schema schema) {
        final String tableName = "AccessRoleFrontendPrivilege";
        final String tableNamespace = FrontendSubsystemConsts.UUID;

        final TField accessRoleId = new TField("access_role_id", new TableReference("AccessRole", CoreSubsystemConsts.UUID));
        final TField privilege = new TField("privilege", Integer.class);
        final TField operations = new TField("operations", Integer.class);

        final THashIndex accessRoleIdHashIndex = new THashIndex(accessRoleId);

        schema.createTable(new Table(
                tableName,
                tableNamespace,
                Arrays.asList(accessRoleId, privilege, operations),
                Collections.singletonList(accessRoleIdHashIndex)
        ));
    }

    private void updateAdministratorAccessRole() {
        executeQuery(new Query<Void>() {
            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private EditableResource<AccessRoleFrontendPrivilegeEditable> accessRoleFrontendEditableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                accessRoleFrontendEditableResource = resources.getEditableResource(AccessRoleFrontendPrivilegeEditable.class);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                accessRoleReadableResource.forEach(accessRole -> {
                    if (accessRole.isAdmin()) {
                        final AccessRoleFrontendPrivilegeEditable accessRoleFrontendPrivilege = accessRoleFrontendEditableResource.create(transaction);
                        accessRoleFrontendPrivilege.setAccessRoleId(accessRole.getId());
                        accessRoleFrontendPrivilege.setPrivilege(FrontendPrivilege.DOCUMENTATION_ACCESS);
                        accessRoleFrontendPrivilege.setOperations(new AccessOperationCollection(AccessOperation.READ));
                        accessRoleFrontendEditableResource.save(accessRoleFrontendPrivilege, transaction);
                    }
                }, transaction);
                return null;
            }
        });
    }
}
