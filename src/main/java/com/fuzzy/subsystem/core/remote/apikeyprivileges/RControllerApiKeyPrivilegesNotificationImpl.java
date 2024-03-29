package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.remote.apikey.ApiKeyPrivilegesSecurityLogger;

public class RControllerApiKeyPrivilegesNotificationImpl extends AbstractQueryRController<CoreSubsystem> implements RControllerApiKeyPrivilegesNotification {

    private ApiKeyPrivilegesSecurityLogger apiKeyPrivilegesSecurityLogger;

    public RControllerApiKeyPrivilegesNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        apiKeyPrivilegesSecurityLogger = new ApiKeyPrivilegesSecurityLogger(resources);
    }

    @Override
    public void onBeforeChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException {
        apiKeyPrivilegesSecurityLogger.saveStateBeforeModifications(apiKeyId, context);
    }

    @Override
    public void onAfterChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException {
        apiKeyPrivilegesSecurityLogger.writeToLog(context);
    }
}
