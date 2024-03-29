package com.fuzzy.subsystem.frontend.remote.employeeauthentication;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthenticationNotifications;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;
import com.fuzzy.subsystems.readableresourcecache.ReadableResourceCache;

import java.util.Objects;

public class RCEmployeeAuthenticationNotificationsImpl extends AbstractQueryRController<FrontendSubsystem>
        implements RCEmployeeAuthenticationNotifications {

    private ReadableResourceCache<AuthenticationReadable> authenticationReadableResource;
    private final SessionServiceEmployee sessionService;

    public RCEmployeeAuthenticationNotificationsImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        authenticationReadableResource = new ReadableResourceCache<>(resources, AuthenticationReadable.class);
        sessionService = component.getSessionService();
    }

    @Override
    public void onAfterAssignAuthenticationToEmployee(long authenticationId, long employeeId, ContextTransaction context) throws PlatformException {

    }

    @Override
    public void onAfterEraseAuthenticationForEmployee(long authenticationId, long employeeId, ContextTransaction context) throws PlatformException {
        if (isIntegratedAuthentication(authenticationId, context)) {
            context.getTransaction().addCommitListener(() -> sessionService.clearSessions(
                    employeeId, CoreSubsystemConsts.UUID, CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, context));
        }
    }

    private boolean isIntegratedAuthentication(long authenticationId, ContextTransaction<?> context) throws PlatformException {
        AuthenticationReadable authentication =
                authenticationReadableResource.get(authenticationId, context.getTransaction());
        return authentication != null
                && Objects.equals(authentication.getType(), CoreSubsystemConsts.AuthenticationTypes.INTEGRATED);
    }
}
