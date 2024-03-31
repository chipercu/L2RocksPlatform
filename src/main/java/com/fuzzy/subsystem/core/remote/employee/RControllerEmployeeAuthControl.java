package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
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
