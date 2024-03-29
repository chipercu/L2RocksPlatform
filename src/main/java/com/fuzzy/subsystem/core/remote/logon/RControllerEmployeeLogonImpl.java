package com.fuzzy.subsystem.core.remote.logon;

import com.google.common.base.Objects;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.*;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.config.AuthenticationConfig;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.LogonType;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory.EmployeeAuthorizationHistoryEditable;
import com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory.EmployeeAuthorizationHistoryReadable;
import com.fuzzy.subsystem.core.logoninfo.LogonInfoManager;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;
import com.fuzzy.subsystem.core.remote.employeeauthenticationchecker.RCEmployeeAuthenticationChecker;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;
import com.fuzzy.subsystem.core.utils.LastAdministratorValidator;

import java.time.Instant;
import java.util.Set;

public class RControllerEmployeeLogonImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerEmployeeLogon {

    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private final ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;
    private final EditableResource<EmployeeAuthorizationHistoryEditable> employeeAuthorizationHistoryEditableResource;
    private final RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
    private final RCEmployeeAuthentication rcEmployeeAuthentication;
    private final CoreConfigGetter coreConfigGetter;
    private final EmployeePrivilegesGetter employeePrivilegesGetter;
    private final LastAdministratorValidator lastAdministratorValidator;
    private final LogonInfoManager logonInfoManager;
    private Instant currentTime;
    private final LogonType logonType;

    public RControllerEmployeeLogonImpl(CoreSubsystem subSystem, ResourceProvider resources) {
        super(subSystem, resources);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
        employeeAuthorizationHistoryEditableResource =
                resources.getEditableResource(EmployeeAuthorizationHistoryEditable.class);
        rcEmployeeAuthenticationChecker =
                resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
        rcEmployeeAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthentication.class);
        coreConfigGetter = new CoreConfigGetter(resources);
        employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
        lastAdministratorValidator = new LastAdministratorValidator(subSystem, resources);
        logonInfoManager = new LogonInfoManager(resources);
        logonType = subSystem.getConfig().getLogonType();
    }

    @Override
    public EmployeeLogin logon(String login, String passwordHash, ContextTransactionRequest context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        this.currentTime = Instant.now();
        EmployeeReadable employee = getEmployee(login, transaction);
        if (employee == null) {
            return buildEmployeeLogin(null, AuthStatus.INVALID_LOGON);
        }
        if (!rcEmployeeAuthenticationChecker.isAssigned(
                CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employee.getId(), context)) {
            return buildEmployeeLogin(employee.getId(), AuthStatus.DISABLED_LOGON);
        }
        HashFilter filter = new HashFilter(
                EmployeeAuthorizationHistoryReadable.FIELD_EMPLOYEE_ID,
                employee.getId()
        );
        EmployeeAuthorizationHistoryEditable employeeAuthorizationHistory =
                employeeAuthorizationHistoryEditableResource.find(filter, transaction);
        if (employeeAuthorizationHistory == null) {
            employeeAuthorizationHistory = employeeAuthorizationHistoryEditableResource.create(transaction);
            employeeAuthorizationHistory.setEmployeeId(employee.getId());
        }
        AuthenticationConfig authenticationConfig = coreConfigGetter.getAuthenticationConfig(transaction);
        if (employee.checkPasswordHash(passwordHash)) {
            if (employeeAuthorizationHistory.getInvalidLogonCount() != null) {
                employeeAuthorizationHistory.setInvalidLogonCount(null);
            }
            if (employeeAuthorizationHistory.getLastInvalidLogonUtcTime() != null) {
                employeeAuthorizationHistory.setLastInvalidLogonUtcTime(null);
            }
        } else {
            EmployeeLogin employeeLogin =
                    processInvalidLogon(employee, employeeAuthorizationHistory, authenticationConfig, context);
            employeeAuthorizationHistoryEditableResource.save(employeeAuthorizationHistory, transaction);
            return employeeLogin;
        }

        if (employee.isNeedToChangePassword()) {
            return buildEmployeeLogin(employee.getId(), AuthStatus.EXPIRED_PASSWORD);
        }

        if (authenticationConfig.getPasswordExpirationTime() != null && employee.hasPassword() &&
                employeeAuthorizationHistory.getLastPasswordChangeUtcTime() != null) {
            Instant passwordExpirationInstant =
                    employeeAuthorizationHistory.getLastPasswordChangeUtcTime()
                            .plus(authenticationConfig.getPasswordExpirationTime());
            if (passwordExpirationInstant.isBefore(currentTime)) {
                return buildEmployeeLogin(employee.getId(), AuthStatus.EXPIRED_PASSWORD);
            }
        }
        String ipAddress = context.getSource().getRequest().getRemoteAddress().endRemoteAddress;
        if (!Objects.equal(ipAddress, employeeAuthorizationHistory.getLastIpAddress())) {
            employeeAuthorizationHistory.setLastIpAddress(ipAddress);
        }
        employeeAuthorizationHistory.setLastLogonUtcTime(currentTime);
        employeeAuthorizationHistoryEditableResource.save(employeeAuthorizationHistory, transaction);
        if (employeePrivilegesGetter.isHaveAnyPrivileges(employee.getId(), context)) {
            logonInfoManager.setLastLogonTime(employee.getId(), Instant.now(), context);
            return buildEmployeeLogin(employee.getId(), AuthStatus.SUCCESS);
        } else {
            return buildEmployeeLogin(employee.getId(), AuthStatus.NO_PRIVILEGES);
        }
    }

    @Override
    public LogonType getLogonType() {
        return logonType;
    }

    private EmployeeReadable getEmployee(String login, QueryTransaction transaction) throws PlatformException {
        if (logonType == LogonType.EMAIL) {
            return employeeReadableResource.find(new HashFilter(EmployeeReadable.FIELD_EMAIL, login), transaction);
        } else if (logonType == LogonType.LOGIN) {
            return employeeReadableResource.find(new HashFilter(EmployeeReadable.FIELD_LOGIN, login), transaction);
        }
        throw new InternalError("Invalid logon type");
    }

    private EmployeeLogin processInvalidLogon(
            EmployeeReadable employee,
            EmployeeAuthorizationHistoryEditable employeeAuthorizationHistory,
            AuthenticationConfig authenticationConfig,
            ContextTransactionRequest context
    ) throws PlatformException {
        if (employeeAuthorizationHistory.getInvalidLogonCount() == null ||
                employeeAuthorizationHistory.getLastInvalidLogonUtcTime() == null ||
                employeeAuthorizationHistory.getLastInvalidLogonUtcTime()
                        .plusSeconds(component.getConfig().getDurationResetCountInvalidLogon().getSeconds())
                        .compareTo(currentTime) < 0) {
            employeeAuthorizationHistory.setInvalidLogonCount(1);
        } else if (!Objects.equal(employeeAuthorizationHistory.getInvalidLogonCount(), Integer.MAX_VALUE)) {
            employeeAuthorizationHistory.setInvalidLogonCount(employeeAuthorizationHistory.getInvalidLogonCount() + 1);
        }
        if (authenticationConfig.getMaxInvalidLogonCount() != null
                && employeeAuthorizationHistory.getInvalidLogonCount() >= authenticationConfig.getMaxInvalidLogonCount()) {
            Set<Long> administrators = lastAdministratorValidator.getAdministrators(context);
            if (administrators.size() > 1 || !administrators.contains(employee.getId())) {
                employeeAuthorizationHistory.setInvalidLogonCount(null);
                QueryTransaction transaction = context.getTransaction();
                HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, employee.getId());
                employeeAuthenticationReadableResource.forEach(filter, employeeAuthentication -> {
                    AuthenticationReadable authentication =
                            authenticationReadableResource.get(employeeAuthentication.getAuthenticationId(), transaction);
                    if (Objects.equal(authentication.getType(), CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)) {
                        rcEmployeeAuthentication.eraseAuthenticationForEmployee(
                                authentication.getId(), employee.getId(), context);
                    }
                }, transaction);
                GEmployeeUpdateEvent.send(component, employee.getId(), transaction);
                employeeAuthorizationHistory.setLastInvalidLogonUtcTime(null);
                return buildEmployeeLogin(employee.getId(), AuthStatus.INVALID_LOGON_AND_MAX_LOGON_ATTEMPTS_EXCEED);
            }
        }
        employeeAuthorizationHistory.setLastInvalidLogonUtcTime(currentTime);
        return buildEmployeeLogin(employee.getId(), AuthStatus.INVALID_LOGON);
    }

    private EmployeeLogin buildEmployeeLogin(Long employeeId, AuthStatus authStatus) {
        return new EmployeeLogin(employeeId, authStatus,
                CoreSubsystemConsts.UUID, CoreSubsystemConsts.AuthenticationTypes.INTEGRATED);
    }
}
