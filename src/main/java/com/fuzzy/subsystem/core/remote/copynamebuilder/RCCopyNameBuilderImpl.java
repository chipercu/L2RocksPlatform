package com.fuzzy.subsystem.core.remote.copynamebuilder;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.utils.LanguageGetter;

public class RCCopyNameBuilderImpl extends AbstractQueryRController<CoreSubsystem> implements RCCopyNameBuilder {

    private final CoreSubsystem component;
    private final LanguageGetter languageGetter;
    private String baseName;
    private int index;

    public RCCopyNameBuilderImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.component = component;
        languageGetter = new LanguageGetter(resources);
    }

    @Override
    public void start(String sourceName, ContextTransaction context) throws PlatformException {
        Language language = languageGetter.get(context);
        String copyLoc = component.getMessageSource().getString(CoreSubsystemConsts.Localization.COPY, language);
        baseName = sourceName + " - " + copyLoc;
        index = 1;
    }

    @Override
    public String next() {
        String name = index == 1 ? baseName : String.format("%s(%s)", baseName, index);
        index++;
        return name;
    }
}
