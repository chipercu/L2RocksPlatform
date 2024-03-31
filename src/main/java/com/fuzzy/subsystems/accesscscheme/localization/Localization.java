package com.fuzzy.subsystems.accesscscheme.localization;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.config.Language;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface Localization {
    @Nullable String getLocalization(@NonNull String locKey, @NonNull Language language) throws PlatformException;
}
