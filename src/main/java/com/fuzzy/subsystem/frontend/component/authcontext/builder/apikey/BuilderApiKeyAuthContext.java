package com.fuzzy.subsystem.frontend.component.authcontext.builder.apikey;

import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.apikeyprivileges.ApiKeyPrivilegesGetter;
import com.fuzzy.subsystem.core.authcontext.system.ApiKeyAuthContext;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.remote.logon.ApiKeyAuthStatus;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.BuilderAuthContext;
import com.fuzzy.subsystem.frontend.exception.FrontendExceptionBuilder;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.util.HashMap;

public abstract class BuilderApiKeyAuthContext implements BuilderAuthContext {

    protected ApiKeyPrivilegesGetter apiKeyPrivilegesGetter;

    private final boolean serviceMode;

    public BuilderApiKeyAuthContext(boolean serviceMode) {
        this.serviceMode = serviceMode;
    }

    @Override
    public AuthorizedContext auth(GRequest gRequest, ContextTransaction context) throws PlatformException {
        ApiKeyReadable apiKey = getApiKey(gRequest, context);
        if (apiKey == null) {
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.ApiKey.TYPE_AUTH),
                    new SyslogStructDataTarget(CoreTarget.TYPE_API_KEY)
                            .withParam(CoreParameter.ApiKey.STATUS, ApiKeyAuthStatus.FAIL.name().toLowerCase()),
                    context
            );
            return null;
        }

        HashMap<String, AccessOperationCollection> privileges = apiKeyPrivilegesGetter.getPrivileges(apiKey.getId(), context);
        if (serviceMode && !supportServiceMode(privileges)) {
            throw FrontendExceptionBuilder.buildServiceModeActivatedException();
        }

        return new ApiKeyAuthContext(
                privileges,
                apiKey.getId(),
                apiKey.getName()
        );
    }

    protected abstract ApiKeyReadable getApiKey(GRequest gRequest, ContextTransaction context) throws PlatformException;

    private boolean supportServiceMode(HashMap<String, AccessOperationCollection> privileges) {
        AccessOperationCollection operations = privileges.get(CorePrivilege.SERVICE_MODE.getUniqueKey());
        return (operations != null && operations.contains(AccessOperation.READ));
    }

    @Override
    public String toString() {
        return "BuilderApiKeyAuthContext";
    }
}
