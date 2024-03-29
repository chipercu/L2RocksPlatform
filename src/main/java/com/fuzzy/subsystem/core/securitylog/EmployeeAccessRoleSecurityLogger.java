package com.fuzzy.subsystem.core.securitylog;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.util.HashSet;
import java.util.Set;

public class EmployeeAccessRoleSecurityLogger {

	private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
	private ReadableResource<EmployeeReadable> employeeReadableResource;
	private ReadableResource<AccessRoleReadable> accessRoleReadableResource;

	private Set<Long> prevAccessRoles;

	public EmployeeAccessRoleSecurityLogger(ResourceProvider resources) {
		employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
		employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
		accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
	}

	public void logEmployeeAccessRole(String eventType, long employeeId, Long accessRoleId, ContextTransaction<?> context)
			throws PlatformException {
		String login = employeeReadableResource.get(employeeId, context.getTransaction()).getLogin();
		AccessRoleReadable accessRole = accessRoleReadableResource.get(accessRoleId, context.getTransaction());
		if (accessRole != null) {
			SecurityLog.info(
					new SyslogStructDataEvent(eventType)
							.withParam(CoreParameter.Employee.ACCESS_ROLE_ID, String.valueOf(accessRoleId))
							.withParam(CoreParameter.Employee.ACCESS_ROLE_NAME, accessRole.getName()),
					new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeId)
							.withParam(CoreParameter.Employee.LOGIN, login),
					context
			);
		}
	}

	public void beforeEmployeeAccessRolesModifications(long employeeId, ContextTransaction<?> context) throws PlatformException {
		prevAccessRoles = new HashSet<>();
		HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employeeId);
		employeeAccessRoleReadableResource.forEach(filter, employeeAccessRole ->
				prevAccessRoles.add(employeeAccessRole.getAccessRoleId()), context.getTransaction());
	}

	public void afterEmployeeAccessRolesModifications(long employeeId, ContextTransaction<?> context) throws PlatformException {
		HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employeeId);
		employeeAccessRoleReadableResource.forEach(filter, employeeAccessRole -> {
			long accessRoleId = employeeAccessRole.getAccessRoleId();
			if (prevAccessRoles.contains(accessRoleId)) {
				prevAccessRoles.remove(accessRoleId);
			} else {
				logEmployeeAccessRole(CoreEvent.Employee.TYPE_ADDING_ACCESS_ROLE, employeeId, accessRoleId, context);
			}
		}, context.getTransaction());
		for (Long accessRoleId : prevAccessRoles) {
			logEmployeeAccessRole(CoreEvent.Employee.TYPE_REMOVING_ACCESS_ROLE, employeeId, accessRoleId, context);
		}
		prevAccessRoles = null;
	}
}
