package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
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