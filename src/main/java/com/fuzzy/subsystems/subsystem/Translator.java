package com.fuzzy.subsystems.subsystem;

import com.fuzzy.main.cluster.Cluster;
import com.fuzzy.main.cluster.component.manager.ManagerComponent;
import com.fuzzy.main.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystems.Subsystems;

import java.util.HashSet;
import java.util.Set;

/**
 * класс Translator используется для перевода строк на указанный язык.
 * при создании экземпляра Translator собирает SubsystemMessageSource
 * со всех подключаемых модулей.
 * для перевода строки Translator итерируется по всем SubsystemMessageSource до тех пор
 * пока перевод не будет найден, в противном случае возвращается исходная строка
 */
public class Translator {

    private final Set<SubsystemMessageSource> messageSources;

    public Translator() {
        Set<Subsystem> subsystemComponents = getSubsystemComponents();
        messageSources = new HashSet<>();
        subsystemComponents.forEach(component -> messageSources.add(component.getMessageSource()));
    }

    public String translate(String string, Language language) {
        for (SubsystemMessageSource resourceSource : messageSources) {
            String translated = resourceSource.getString(string, language);
            if (!translated.equals(string)) {
                return translated;
            }
        }
        return string;
    }

    private Set<Subsystem> getSubsystemComponents() {
        Set<Subsystem> components = new HashSet<>();
        Cluster cluster = Subsystems.getInstance().getCluster();
        ManagerComponent managerComponent = cluster.getAnyLocalComponent(ManagerComponent.class);
        for (RuntimeComponentInfo runtimeComponentInfo : managerComponent.getRegisterComponent().getLocalComponents()) {
            String uuid = runtimeComponentInfo.uuid;
            Component component = cluster.getAnyLocalComponent(uuid);
            if (component != null) {
                if (component instanceof Subsystem subsystem) {
                    components.add(subsystem);
                }
            }
        }
        return components;
    }
}