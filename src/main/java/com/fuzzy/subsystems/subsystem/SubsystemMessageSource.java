package com.fuzzy.subsystems.subsystem;

import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystems.i18n.MessageResourceSource;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SubsystemMessageSource {

    private final Map<Language, ResourceBundle> resourceBundles;

    public SubsystemMessageSource(Subsystem subsystem) {
        this.resourceBundles = new HashMap<>();
        for (Language language : Language.values()) {
            resourceBundles.put(language, MessageResourceSource.get(subsystem.getInfo().getUuid(), language.getLocale()));
        }
    }

    public String getString(String key, Language language) {
        try {
            return resourceBundles.get(language).getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
