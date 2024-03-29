package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.AuthenticationConfig;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationContent;
import com.fuzzy.subsystem.core.graphql.query.authentication.GIntegratedAuthenticationContent;

import java.util.Objects;

public class RCAuthenticationContentImpl extends AbstractQueryRController<CoreSubsystem>
        implements RCAuthenticationContent {

    private final CoreConfigGetter coreConfigGetter;

    public RCAuthenticationContentImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        coreConfigGetter = new CoreConfigGetter(resources);
    }

    @Override
    public GAuthenticationContent getContent(long authenticationId, String type, ContextTransaction context) throws PlatformException {
        if (!Objects.equals(type, CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)) {
            return null;
        }
        AuthenticationConfig authenticationConfig = coreConfigGetter.getAuthenticationConfig(context.getTransaction());
        return new GIntegratedAuthenticationContent(authenticationConfig);
    }
}
