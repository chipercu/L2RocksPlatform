package com.fuzzy.subsystem.frontend.remote.accessroleprivileges;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivilegesNotification;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RControllerAccessRolePrivilegesNotificationImpl extends AbstractQueryRController<FrontendSubsystem>
        implements RControllerAccessRolePrivilegesNotification {

    private static ConcurrentMap<QueryTransaction, Collection<Long>> noAccessEmployees = new ConcurrentHashMap<>();

    private final SessionServiceEmployee sessionService;
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final EmployeePrivilegesGetter employeePrivilegesGetter;

    public RControllerAccessRolePrivilegesNotificationImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        sessionService = component.getSessionService();
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
    }

    @Override
    public void onBeforeChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException {

    }

    @Override
    public void onAfterChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        List<Long> employees = new ArrayList<>();
        try (IteratorEntity<EmployeeAccessRoleReadable> ie =
                     employeeAccessRoleReadableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                long employeeId = ie.next().getEmployeeId();
                HashMap<String, AccessOperationCollection> privileges =
                        employeePrivilegesGetter.getPrivileges(employeeId, context);
                boolean emptyOperations = true;
                for (AccessOperationCollection operations : privileges.values()) {
                    if (!operations.isEmpty()) {
                        emptyOperations = false;
                        break;
                    }
                }
                if (emptyOperations) {
                    employees.add(employeeId);
                }
            }
        }
        if (employees.isEmpty()) {
            noAccessEmployees.remove(transaction);
        } else {
            noAccessEmployees.put(transaction, employees);
        }
        transaction.addCommitListener(() -> {
            Collection<Long> employeeIds = noAccessEmployees.remove(transaction);
            if (employeeIds != null) {
                for (Long employeeId : employeeIds) {
                    sessionService.clearSessions(employeeId, context);
                }
            }
        });
        transaction.addRollbackListener(cause -> noAccessEmployees.remove(transaction));
    }
}
