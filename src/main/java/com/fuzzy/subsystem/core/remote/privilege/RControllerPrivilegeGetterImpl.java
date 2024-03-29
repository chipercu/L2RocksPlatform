package com.fuzzy.subsystem.core.remote.privilege;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystems.access.AccessOperationCollection;

public class RControllerPrivilegeGetterImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerPrivilegeGetter {

    private CoreSubsystem coreSubsystem;

    public RControllerPrivilegeGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.coreSubsystem = component;
    }

    @Override
    public String getPrivilegeDisplayName(String privilegeKey, Language language, ContextTransaction context) {
        return coreSubsystem.getMessageSource().getString(privilegeKey, language);
    }

    @Override
    public AccessOperationCollection getAvailableOperations(String privilegeKey, ContextTransaction context)
            throws PlatformException {
        CorePrivilege privilege = CorePrivilege.ofKey(privilegeKey);
        return privilege != null ? privilege.getAvailableOperations() : null;
    }
}
