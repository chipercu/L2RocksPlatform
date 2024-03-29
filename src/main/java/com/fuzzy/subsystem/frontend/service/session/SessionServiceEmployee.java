package com.fuzzy.subsystem.frontend.service.session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.component.frontend.context.source.impl.SourceGRequestAuthImpl;
import com.fuzzy.main.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.main.platform.component.frontend.request.GRequestWebSocket;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.Context;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.remote.logon.AuthStatus;
import com.fuzzy.subsystem.core.remote.logon.LogoutCause;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.exception.FrontendExceptionBuilder;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class SessionServiceEmployee implements SessionService {

	private final FrontendSubsystem component;
	private final Cache<String, SessionEmployee> sessions;
	private final SessionTimeoutNotificator timeoutNotificator;
	private final long sessionTimeout;

	public SessionServiceEmployee(FrontendSubsystem frontendSubsystem) {
		this.component = frontendSubsystem;
		sessionTimeout = frontendSubsystem.getConfig().getSessionTimeout().toMillis();
		this.sessions = CacheBuilder.newBuilder()
				.expireAfterAccess(sessionTimeout, TimeUnit.MILLISECONDS)
				.build();
		timeoutNotificator = startTimeoutNotificator();
	}

	public EmployeeSessionAuthContext auth(
			EmployeeReadable employee,
			String componentUuid,
			String authenticationType,
			Map<String, String> params,
			EmployeePrivilegesGetter employeePrivilegesGetter,
			ContextTransactionRequest context
	) throws PlatformException {
		GRequest gRequest = context.getSource().getRequest();
		String sessionUuid;
		if (gRequest instanceof GRequestHttp) {
			sessionUuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		} else if (gRequest instanceof GRequestWebSocket) {
			GRequestWebSocket gRequestWebSocket = (GRequestWebSocket) gRequest;
			sessionUuid = gRequestWebSocket.getSessionUuid();
		} else {
			throw new RuntimeException("Unknown type request: " + gRequest);
		}

		HashMap<String, AccessOperationCollection> privileges =
				employeePrivilegesGetter.getPrivileges(employee.getId(), context);
		if (component.getConfig().isServiceMode()) {
			AccessOperationCollection operations = privileges.get(CorePrivilege.SERVICE_MODE.getUniqueKey());
			if (operations == null || !operations.contains(AccessOperation.READ)) {
				throw FrontendExceptionBuilder.buildDeniedLogonInServiceModeException(component.getConfig().getServiceModeMessage());
			}
		}

		SessionEmployee session = new SessionEmployee(sessionUuid, componentUuid, authenticationType,
				employee.getLogin(), employee.getId(), params);
		sessions.put(sessionUuid, session);
		timeoutNotificator.putSessionInfo(sessionUuid, session);
		timeoutNotificator.addSessionTimeout(sessionUuid);

		EmployeeSessionAuthContext authContextUser = new EmployeeSessionAuthContext(
				privileges,
				employee.getId(),
				employee.getLogin(),
				sessionUuid
		);
		authContextUser.addParams(params);
		((SourceGRequestAuthImpl) context.getSource()).setAuthContext(authContextUser);

		//Логируем
		SecurityLog.info(
				new SyslogStructDataEvent(CoreEvent.Employee.TYPE_LOGON)
						.withParam(CoreParameter.Employee.STATUS, AuthStatus.SUCCESS.name().toLowerCase())
						.withParam(CoreParameter.Employee.SESSION_HASH, Session.getHash(sessionUuid)),
				session.getSysLogDataTarget(),
				context
		);
 		return authContextUser;
	}

	public EmployeeSessionAuthContext auth(
			EmployeeReadable employee,
			String componentUuid,
			String authenticationType,
			EmployeePrivilegesGetter employeePrivilegesGetter,
			ContextTransactionRequest context
	) throws PlatformException {
		return auth(employee, componentUuid, authenticationType, Collections.emptyMap(), employeePrivilegesGetter, context);
	}

	public SessionEmployee getAuthSession(String sessionUuid) {
		SessionEmployee session = sessions.getIfPresent(sessionUuid);
		if (session == null) return null;
		session.updateAccessTime();
		timeoutNotificator.addSessionTimeout(sessionUuid);
		return session;
	}

	public void logout(ContextTransactionRequest context) {
		EmployeeSessionAuthContext authContextUser = (EmployeeSessionAuthContext) context.getSource().getAuthContext();
		String sessionUuid = authContextUser.getSessionId();
		SessionEmployee session = sessions.getIfPresent(sessionUuid);
		String login = session == null ? null : session.login;
		sessions.invalidate(sessionUuid);
		timeoutNotificator.removeSessionTimeout(new SessionTimeout(sessionUuid, null));
		timeoutNotificator.removeSessionInfo(sessionUuid);

		//Логируем
		SecurityLog.info(
				new SyslogStructDataEvent(CoreEvent.Employee.TYPE_LOGOUT)
						.withParam(CoreParameter.Employee.CAUSE, LogoutCause.MANUAL.name().toLowerCase())
						.withParam(CoreParameter.Employee.SESSION_HASH, Session.getHash(sessionUuid)),
				new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, authContextUser.getEmployeeId())
						.withParam(CoreParameter.Employee.LOGIN, login),
				context
		);
	}

	public void clearSessions(long employeeId, Context<?> context) {
		clearSessions(employeeId, null, context);
	}

	public void clearSessions(long employeeId, String exceptSessionId, Context<?> context) {
		clearSessions(employeeId, null, null, exceptSessionId, context);
	}

	public void clearSessions(long employeeId, String componentUuid, String authenticationType, Context<?> context) {
		clearSessions(employeeId, componentUuid, authenticationType, null, context);
	}

	public Collection<SessionEmployee> getSessions() {
		return sessions.asMap().values();
	}

	@Override
	public ConcurrentMap<String, SessionEmployee> getSessionsAsMap() {
		return sessions.asMap();
	}

	@Override
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	private void clearSessions(long employeeId,
							   String componentUuid,
							   String authenticationType,
							   String exceptSessionId,
							   Context<?> context) {
		timeoutNotificator.clear();
		ConcurrentMap<String, SessionEmployee> sessionMap = sessions.asMap();
		sessionMap.forEach((sessionId, session) -> {
			if (session.employeeId == employeeId
					&& (componentUuid == null || componentUuid.equals(session.getComponentUuid()))
					&& (authenticationType == null || authenticationType.equals(session.getAuthenticationType()))
					&& !sessionId.equals(exceptSessionId)) {
				sessionMap.remove(sessionId);

				//Логируем
				SecurityLog.info(
						new SyslogStructDataEvent(CoreEvent.Employee.TYPE_LOGOUT)
								.withParam(CoreParameter.Employee.CAUSE, LogoutCause.FORCE.name().toLowerCase())
								.withParam(CoreParameter.Employee.SESSION_HASH, Session.getHash(session.uuid)),
						session.getSysLogDataTarget(),
						context
				);
			}
		});
	}

	private SessionTimeoutNotificator startTimeoutNotificator() {
		SessionTimeoutNotificator timeoutNotificator = new SessionTimeoutNotificator(this, "EmployeeAuth");
		timeoutNotificator.setDaemon(true);
		timeoutNotificator.setName("SessionTimeoutNotificator");
		timeoutNotificator.start();
		return timeoutNotificator;
	}
}
