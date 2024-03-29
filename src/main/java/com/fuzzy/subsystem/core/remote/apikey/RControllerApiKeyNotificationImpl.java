package com.fuzzy.subsystem.core.remote.apikey;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeEditable;

public class RControllerApiKeyNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerApiKeyNotification {

    private RemovableResource<ApiKeyCorePrivilegeEditable> apiKeyCorePrivilegeRemovableResource;

    public RControllerApiKeyNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        apiKeyCorePrivilegeRemovableResource = resources.getRemovableResource(ApiKeyCorePrivilegeEditable.class);
    }

    @Override
    public void onBeforeRemoveApiKey(Long apiKeyId, ContextTransaction context) throws PlatformException {
        apiKeyCorePrivilegeRemovableResource.removeAll(new HashFilter(
                ApiKeyCorePrivilegeEditable.FIELD_API_KEY_ID,
                apiKeyId
        ), context.getTransaction());
    }
}
