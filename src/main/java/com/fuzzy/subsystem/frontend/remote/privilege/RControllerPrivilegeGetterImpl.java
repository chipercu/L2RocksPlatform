package com.fuzzy.subsystem.frontend.remote.privilege;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.remote.privilege.RControllerPrivilegeGetter;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.Objects;

public class RControllerPrivilegeGetterImpl extends AbstractQueryRController<FrontendSubsystem> implements RControllerPrivilegeGetter {

    private FrontendSubsystem frontendSubsystem;

    public RControllerPrivilegeGetterImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.frontendSubsystem = component;
    }

    @Override
    public String getPrivilegeDisplayName(String privilegeKey, Language language, ContextTransaction context) throws PlatformException {
        return frontendSubsystem.getMessageSource().getString(privilegeKey, language);
    }

    @Override
    public AccessOperationCollection getAvailableOperations(String privilegeKey, ContextTransaction context) throws PlatformException {
        final FrontendPrivilege privilege = FrontendPrivilege.ofKey(privilegeKey);
        return Objects.nonNull(privilege) ? privilege.getAvailableOperations() : null;
    }
}
