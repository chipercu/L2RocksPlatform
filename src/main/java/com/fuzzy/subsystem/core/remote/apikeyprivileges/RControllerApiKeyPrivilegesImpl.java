package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.domainobject.apikeyprivilege.ApiKeyCorePrivilegeEditable;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeSetter;
import com.fuzzy.subsystems.entityprivilege.PrivilegeOperationsPair;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RControllerApiKeyPrivilegesImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerApiKeyPrivileges {

    private ReadableResource<ApiKeyReadable> apiKeyReadableResource;
    private EntityPrivilegeSetter<CorePrivilege, ApiKeyCorePrivilegeEditable> apiKeyPrivilegeSetter;
    private Set<RControllerApiKeyPrivilegesNotification> rControllerApiKeyPrivilegesNotifications;

    public RControllerApiKeyPrivilegesImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        apiKeyReadableResource = resources.getReadableResource(ApiKeyReadable.class);
        apiKeyPrivilegeSetter = new EntityPrivilegeSetter<CorePrivilege, ApiKeyCorePrivilegeEditable>(
                ApiKeyCorePrivilegeEditable.class,
                resources
        ) {
            @Override
            protected void setEntityId(ApiKeyCorePrivilegeEditable object, long apiKeyId) {
                object.setApiKeyId(apiKeyId);
            }

            @Override
            protected int getEntityFiledNumber() {
                return ApiKeyCorePrivilegeEditable.FIELD_API_KEY_ID;
            }
        };

        rControllerApiKeyPrivilegesNotifications = resources.getQueryRemoteControllers(RControllerApiKeyPrivilegesNotification.class);
    }

    @Override
    public void setPrivilegesToApiKey(long apiKeyId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException {
        new PrimaryKeyValidator(false).validate(apiKeyId, apiKeyReadableResource, context.getTransaction());

        for (RControllerApiKeyPrivilegesNotification rControllerApiKeyPrivilegesNotification : rControllerApiKeyPrivilegesNotifications) {
            rControllerApiKeyPrivilegesNotification.onBeforeChangePrivileges(apiKeyId, context);
        }

        List<PrivilegeOperationsPair<CorePrivilege>> privilegeOperationsPairs = new ArrayList<>();
        for (PrivilegeValue privilegeValue : privilegeValues) {
            CorePrivilege privilege = CorePrivilege.ofKey(privilegeValue.getKey());
            if (privilege != null) {
                switch (privilege) {
                    case GRAPHQL_TOOL:
                    case ACCESS_ROLE:
                    case PRIVATE_SETTINGS:
                        throw GeneralExceptionBuilder.buildInvalidValueException("privilege", privilege.getUniqueKey());
                }
                privilegeOperationsPairs.add(new PrivilegeOperationsPair<>(privilege, privilegeValue.getOperations()));
            }
        }
        apiKeyPrivilegeSetter.setPrivilegesToEntity(apiKeyId, privilegeOperationsPairs, context.getTransaction());

        for (RControllerApiKeyPrivilegesNotification rControllerApiKeyPrivilegesNotification : rControllerApiKeyPrivilegesNotifications) {
            rControllerApiKeyPrivilegesNotification.onAfterChangePrivileges(apiKeyId, context);
        }
    }
}
