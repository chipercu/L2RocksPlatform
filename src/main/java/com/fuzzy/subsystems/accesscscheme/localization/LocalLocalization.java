package com.fuzzy.subsystems.accesscscheme.localization;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystems.subsystem.Subsystem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LocalLocalization  implements Localization{

    private final Subsystem component;

    public LocalLocalization(@NonNull Subsystem component) {
        this.component = component;
    }

    @Override
    public @Nullable String getLocalization(@NonNull String locKey, @NonNull Language language) throws PlatformException {
        return component.getMessageSource().getString(locKey, language);
    }
}
