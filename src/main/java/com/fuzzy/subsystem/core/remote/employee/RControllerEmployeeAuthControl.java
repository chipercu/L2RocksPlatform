package com.fuzzy.subsystem.core.remote.employee;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public interface RControllerEmployeeAuthControl extends QueryRemoteController {

	EmployeeSessionAuthContext auth(@NonNull EmployeeReadable employee,
									@NonNull String componentUuid,
									@NonNull String authenticationType,
									@NonNull HashMap<String, String> params,
									@NonNull ContextTransactionRequest context) throws PlatformException;
}
