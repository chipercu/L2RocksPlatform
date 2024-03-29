package com.fuzzy.subsystem.core.remote.authenticationtype;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RCAuthenticationTypeImpl extends AbstractQueryRController<CoreSubsystem> implements RCAuthenticationType {

    private final CoreSubsystem component;
    private final ReadableResource<AuthenticationReadable> authenticationReadableResource;

    public RCAuthenticationTypeImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.component = component;
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
    }

    @Override
    public ArrayList<String> getTypes(ContextTransaction context) {
        return new ArrayList<>(List.of(CoreSubsystemConsts.AuthenticationTypes.INTEGRATED));
    }

    @Override
    public ArrayList<String> getAvailableForCreatingType(ContextTransaction context) throws PlatformException {
        ArrayList<String> coreTypes = getTypes(context);
        ArrayList<String> availableForCreatingTypes = new ArrayList<>();
        for (String coreType : coreTypes) {
            HashFilter typeHashFilter = new HashFilter(AuthenticationReadable.FIELD_TYPE, coreType);
            if (Objects.isNull(authenticationReadableResource.find(typeHashFilter, context.getTransaction()))) {
                availableForCreatingTypes.add(coreType);
            }
        }
        return availableForCreatingTypes;
    }

    @Override
    public String getLocalization(String type, Language language, ContextTransaction context) {
        if (Objects.equals(type, CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)) {
            return component.getMessageSource().getString(
                    CoreSubsystemConsts.Localization.AuthenticationType.INTEGRATED, language);
        }
        return null;
    }
}
