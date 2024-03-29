package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreSubsystem;

import java.util.Set;

public class RControllerMailConfigGetterWrapperImpl extends AbstractQueryRController<CoreSubsystem> implements RControllerMailConfigGetterWrapper {

    private final Set<RControllerMailConfigGetter> mailConfigGetters;

    public RControllerMailConfigGetterWrapperImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        mailConfigGetters = resources.getQueryRemoteControllers(RControllerMailConfigGetter.class);
    }

    @Override
    public Boolean isMailConfigured() throws PlatformException {
        for (RControllerMailConfigGetter mailConfigGetter : mailConfigGetters) {
            return mailConfigGetter.isMailConfigured();
        }
        return false;
    }
}