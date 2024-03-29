package com.fuzzy.subsystem.core.securitylog;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessReadable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessReadable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.entityelements.Elements;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ManagerEmployeeAccessSecurityLogger {

    private ReadableResource<EmployeeReadable> employeeReadableResource;
    private ReadableResource<ManagerAllAccessReadable> managerAllAccessReadableResource;
    private ReadableResource<ManagerDepartmentAccessReadable> managerDepartmentAccessReadableResource;
    private ReadableResource<ManagerEmployeeAccessReadable> managerEmployeeAccessReadableResource;
    private DepartmentGrouping departmentGrouping;
    private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

    private Map<Long, Set<Long>> prevEmployeeManagers = null;
    private Elements prevAccess = null;

    public ManagerEmployeeAccessSecurityLogger(ResourceProvider resources) {
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        managerAllAccessReadableResource = resources.getReadableResource(ManagerAllAccessReadable.class);
        managerDepartmentAccessReadableResource = resources.getReadableResource(ManagerDepartmentAccessReadable.class);
        managerEmployeeAccessReadableResource = resources.getReadableResource(ManagerEmployeeAccessReadable.class);
        departmentGrouping = new DepartmentGrouping(resources);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public void afterCreateEmployee(long employeeId, ContextTransaction<?> context) throws PlatformException {
        Set<Long> managers = new HashSet<>();
        addEmployeeManagersTo(managers, employeeId, context.getTransaction());
        for (Long managerId : managers) {
            writeLog(managerId, employeeId, true, context);
        }
    }

    public void beforeRemoveEmployee(long employeeId, ContextTransaction<?> context) throws PlatformException {
        Set<Long> managers = new HashSet<>();
        addEmployeeManagersTo(managers, employeeId, context.getTransaction());
        for (Long managerId : managers) {
            writeLog(managerId, employeeId, false, context);
        }
    }

    public void beforeMoveEmployee(long employeeId, ContextTransaction<?> context) throws PlatformException {
        Set<Long> managers = new HashSet<>();
        addEmployeeManagersTo(managers, employeeId, context.getTransaction());
        prevEmployeeManagers = new HashMap<>();
        prevEmployeeManagers.put(employeeId, managers);
    }

    public void afterMoveEmployee(long employeeId, ContextTransaction<?> context) throws PlatformException {
        Set<Long> prevManagers = prevEmployeeManagers.get(employeeId);
        Set<Long> nextManagers = new HashSet<>();
        addEmployeeManagersTo(nextManagers, employeeId, context.getTransaction());
        writeLog(employeeId, prevManagers, nextManagers, context);
        prevEmployeeManagers = null;
    }

    public void beforeMoveDepartment(long departmentId, ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        prevEmployeeManagers = new HashMap<>();
        departmentGrouping.forEachChildItemRecursively(departmentId, transaction, employeeId -> {
            prevEmployeeManagers.put(employeeId, new HashSet<>());
            return true;
        });
        for (Map.Entry<Long, Set<Long>> entry : prevEmployeeManagers.entrySet()) {
            addEmployeeManagersTo(entry.getValue(), entry.getKey(), transaction);
        }
    }

    public void afterMoveDepartment(long departmentId, ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        Set<Long> nextManagers = new HashSet<>();
        for (Map.Entry<Long, Set<Long>> entry : prevEmployeeManagers.entrySet()) {
            long employeeId = entry.getKey();
            Set<Long> prevManagers = entry.getValue();
            addEmployeeManagersTo(nextManagers, employeeId, transaction);
            writeLog(employeeId, prevManagers, nextManagers, context);
            nextManagers.clear();
        }
        prevEmployeeManagers = null;
    }

    public void beforeAccessModifications(long managerId, ContextTransaction<?> context) throws PlatformException {
        prevAccess = managerEmployeeAccessGetter.getAccess(managerId, context.getTransaction()).getElements();
    }

    public void afterAccessModifications(long managerId, ContextTransaction<?> context) throws PlatformException {
        Elements nextAccess = managerEmployeeAccessGetter.getAccess(managerId, context.getTransaction()).getElements();
        writeLog(managerId, prevAccess, nextAccess, context);
        prevAccess = null;
    }

    public void beforeEmployeesMerge(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction<?> context)
            throws PlatformException {
        for (Long secondaryEmployeeId : secondaryEmployees) {
            beforeRemoveEmployee(secondaryEmployeeId, context);
        }
        beforeAccessModifications(mainEmployeeId, context);
    }

    public void afterEmployeesMerge(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction<?> context)
            throws PlatformException {
        afterAccessModifications(mainEmployeeId, context);
    }

    private void addEmployeeManagersTo(Set<Long> managers, long employeeId, QueryTransaction transaction) throws PlatformException {
        EmployeeReadable employee = employeeReadableResource.get(employeeId, transaction);
        addDepartmentManagersTo(managers, employee.getDepartmentId(), transaction);
        addDirectEmployeeManagersTo(managers, employeeId, transaction);
    }

    private void addDepartmentManagersTo(Set<Long> managers, Long departmentId, QueryTransaction transaction)
            throws PlatformException {
        if (departmentId != null) {
            departmentGrouping.forEachParentOfNodeRecursively(departmentId, transaction, parentDepartmentId -> {
                addDirectDepartmentManagersTo(managers, parentDepartmentId, transaction);
                return true;
            });
            addDirectDepartmentManagersTo(managers, departmentId, transaction);
        }
        managerAllAccessReadableResource.forEach(managerAllAccess -> managers.add(managerAllAccess.getManagerId()), transaction);
    }

    private void addDirectDepartmentManagersTo(Set<Long> managers, Long departmentId, QueryTransaction transaction)
            throws PlatformException {
        HashFilter filter = new HashFilter(ManagerDepartmentAccessReadable.FIELD_DEPARTMENT_ID, departmentId);
        managerDepartmentAccessReadableResource.forEach(
                filter,
                managerDepartmentAccess -> managers.add(managerDepartmentAccess.getManagerId()),
                transaction
        );
    }

    private void addDirectEmployeeManagersTo(Set<Long> managers, long employeeId, QueryTransaction transaction)
            throws PlatformException {
        HashFilter filter = new HashFilter(ManagerEmployeeAccessReadable.FIELD_EMPLOYEE_ID, employeeId);
        managerEmployeeAccessReadableResource.forEach(
                filter,
                managerEmployeeAccess -> managers.add(managerEmployeeAccess.getManagerId()),
                transaction
        );
    }

    private void writeLog(long managerId, Elements prevAccess, Elements nextAccess, ContextTransaction<?> context)
            throws PlatformException {
        if (!prevAccess.isAll && nextAccess.isAll) {
            writeLog(managerId, (Long) null, true, context);
        } else if (prevAccess.isAll && !nextAccess.isAll) {
            writeLog(managerId, (Long) null, false, context);
            writeLog(managerId, nextAccess, true, context);
        } else if (!prevAccess.isAll) {
            if (prevAccess.items == null || prevAccess.items.isEmpty()) {
                writeLog(managerId, nextAccess, true, context);
            } else if (nextAccess.items == null || nextAccess.items.isEmpty()) {
                writeLog(managerId, prevAccess, false, context);
            } else {
                for (Long prevEmployeeId : prevAccess.items) {
                    if (!nextAccess.items.contains(prevEmployeeId)) {
                        writeLog(managerId, prevEmployeeId, false, context);
                    }
                }
                for (Long nextEmployeeId : nextAccess.items) {
                    if (!prevAccess.items.contains(nextEmployeeId)) {
                        writeLog(managerId, nextEmployeeId, true, context);
                    }
                }
            }
        }
    }

    private void writeLog(
            long managerId, Elements managerEmployeeAccess, boolean adding, ContextTransaction<?> context)
            throws PlatformException {
        if (managerEmployeeAccess.isAll) {
            writeLog(managerId, (Long) null, adding, context);
        } else if (managerEmployeeAccess.items != null) {
            for (Long employeeId : managerEmployeeAccess.items) {
                writeLog(managerId, employeeId, adding, context);
            }
        }
    }

    private void writeLog(long employeeId, Set<Long> beforeManagers, Set<Long> afterManagers, ContextTransaction<?> context)
            throws PlatformException {
        for (Long managerId : beforeManagers) {
            if (!afterManagers.contains(managerId)) {
                writeLog(managerId, employeeId, false, context);
            }
        }
        for (Long managerId : afterManagers) {
            if (!beforeManagers.contains(managerId)) {
                writeLog(managerId, employeeId, true, context);
            }
        }
    }

    private void writeLog(long managerId, Long employeeId, boolean adding, ContextTransaction<?> context)
            throws PlatformException {
        SyslogStructDataEvent syslogStructDataEvent = new SyslogStructDataEvent(
                adding ? CoreEvent.Employee.TYPE_ADDING_ACCESS_TO_EMPLOYEE : CoreEvent.Employee.TYPE_REMOVING_ACCESS_TO_EMPLOYEE
        );
        if (employeeId != null) {
            String login = employeeReadableResource.get(employeeId, context.getTransaction()).getLogin();
            syslogStructDataEvent
                    .withParam(CoreParameter.Employee.EMPLOYEE_ID, employeeId.toString())
                    .withParam(CoreParameter.Employee.LOGIN, login);
        } else {
            syslogStructDataEvent.withParam(CoreParameter.Employee.ALL, String.valueOf(true));
        }
        String login = employeeReadableResource.get(managerId, context.getTransaction()).getLogin();
        SecurityLog.info(
                syslogStructDataEvent,
                new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, managerId)
                        .withParam(CoreParameter.Employee.LOGIN, login),
                context
        );
    }
}
