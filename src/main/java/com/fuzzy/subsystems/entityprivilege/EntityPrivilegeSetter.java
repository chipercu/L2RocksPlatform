package com.fuzzy.subsystems.entityprivilege;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.RemovableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.PrivilegeEnum;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityPrivilegeSetter<
        T extends PrivilegeEnum,
        Y extends DomainObject & EntityPrivilegeEditable<T> & DomainObjectEditable> {

    private final RemovableResource<Y> removableResource;

    public EntityPrivilegeSetter(Class<Y> yClass, ResourceProvider resources) {
        removableResource = resources.getRemovableResource(yClass);
    }

    public void setPrivilegesToEntity(
            long entityId,
            List<PrivilegeOperationsPair<T>> privilegeOperationsPairs,
            QueryTransaction transaction
    ) throws PlatformException {
        List<Y> accessRolePrivileges = new ArrayList<>();
        HashFilter filter = new HashFilter(getEntityFiledNumber(), entityId);
        try (IteratorEntity<Y> ie = removableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                accessRolePrivileges.add(ie.next());
            }
        }
        for (PrivilegeOperationsPair<T> privilegeOperationsPair : privilegeOperationsPairs) {
            AccessOperationCollection operations = privilegeOperationsPair.getOperations();
            if (operations.isEmpty()) {
                continue;
            }
            boolean found = false;
            for (int i = 0; i < accessRolePrivileges.size(); i++) {
                Y accessRolePrivilege = accessRolePrivileges.get(i);
                if (accessRolePrivilege != null &&
                        accessRolePrivilege.getPrivilege() == privilegeOperationsPair.getPrivilege()) {
                    found = true;
                    if (!accessRolePrivilege.getOperations().equals(operations)) {
                        accessRolePrivilege.setOperations(operations);
                        removableResource.save(accessRolePrivilege, transaction);
                    }
                    accessRolePrivileges.set(i, null);
                    break;
                }
            }
            if (!found) {
                Y accessRolePrivilege = removableResource.create(transaction);
                setEntityId(accessRolePrivilege, entityId);
                accessRolePrivilege.setPrivilege(privilegeOperationsPair.getPrivilege());
                accessRolePrivilege.setOperations(operations);
                removableResource.save(accessRolePrivilege, transaction);
            }
        }
        for (Y accessRolePrivilege : accessRolePrivileges) {
            if (accessRolePrivilege != null) {
                removableResource.remove(accessRolePrivilege, transaction);
            }
        }
    }

    protected abstract void setEntityId(Y object, long entityId);

    protected abstract int getEntityFiledNumber();
}
