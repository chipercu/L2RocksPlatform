package com.fuzzy.subsystem.frontend.service.session;

import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Map;

public class SessionEmployee extends Session {

	public final String login;
	public final long employeeId;

	public SessionEmployee(@NonNull String uuid,
						   @NonNull String componentUuid,
						   @NonNull String authenticationType,
						   @NonNull String login,
						   long employeeId,
						   @NonNull Map<String, String> params) {
		super(uuid, componentUuid, authenticationType, params);
		this.login = login;
		this.employeeId = employeeId;
	}

	public SessionEmployee(@NonNull String uuid,
						   @NonNull String componentUuid,
						   @NonNull String authenticationType,
						   @NonNull String login,
						   long employeeId) {
		this(uuid, componentUuid, authenticationType, login, employeeId, Collections.emptyMap());
	}

	@Override
	public SyslogStructDataTarget getSysLogDataTarget() {
		return new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeId)
				.withParam(CoreParameter.Employee.LOGIN, login);
	}
}
