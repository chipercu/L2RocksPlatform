package com.fuzzy.subsystems.accesscscheme.localization;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.remote.localization.RCLocalization;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Objects;

public class GlobalLocalization implements Localization {

    private final Component component;

    public GlobalLocalization(@NonNull Component component) {
        this.component = component;
    }

    @Override
    public @NonNull String getLocalization(@NonNull String locKey, @NonNull Language language) throws PlatformException {
        Collection<RCLocalization> rcLocalizations = component.getRemotes().getControllers(RCLocalization.class);
        for (RCLocalization rc : rcLocalizations) {
            String loc = rc.getLocalization(locKey, language);
            if (loc != null && !Objects.equals(loc, locKey)) {
                return loc;
            }
        }
        return locKey;
    }
}
