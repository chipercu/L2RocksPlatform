package com.fuzzy.subsystem.core.remote.localization;

import com.fuzzy.cluster.core.remote.AbstractRController;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.config.Language;

public class RCLocalizationImpl extends AbstractRController<CoreSubsystem> implements RCLocalization {

    private final CoreSubsystem component;

    public RCLocalizationImpl(CoreSubsystem component) {
        super(component);
        this.component = component;
    }

    @Override
    public String getLocalization(String locKey, Language language) {
        return component.getMessageSource().getString(locKey, language);
    }
}
