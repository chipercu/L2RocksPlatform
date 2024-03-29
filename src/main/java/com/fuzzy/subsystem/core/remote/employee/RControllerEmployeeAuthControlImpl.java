package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public class RControllerEmployeeAuthControlImpl extends AbstractQueryRController<CoreSubsystem>
		implements RControllerEmployeeAuthControl {

	private SessionServiceEmployee sessionService;
	private EmployeePrivilegesGetter employeePrivilegesGetter;

	public RControllerEmployeeAuthControlImpl(CoreSubsystem component, ResourceProvider resources) {
		super(component, resources);
		employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
		this.sessionService = Subsystems.getInstance().getCluster()
				.getAnyLocalComponent(FrontendSubsystem.class).getSessionService();
	}

	@Override
	public EmployeeSessionAuthContext auth(@NonNull EmployeeReadable employeeReadable,
										   @NonNull String componentUuid,
										   @NonNull String authenticationType,
										   @NonNull HashMap<String, String> params,
										   @NonNull ContextTransactionRequest context) throws PlatformException{
		return sessionService.auth(employeeReadable, componentUuid, authenticationType, params, employeePrivilegesGetter, context);
	}
}
