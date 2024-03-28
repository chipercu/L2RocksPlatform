package com.fuzzy.main.platform.update.core;

import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.component.version.Version;

public class ModuleUpdateEntity {

    private final Version oldVersion;
    private final Version newVersion;
    private final String componentUuid;

    private Component component;

    public ModuleUpdateEntity(Version oldVersion, Version newVersion, String componentUuid) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.componentUuid = componentUuid;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Version getOldVersion() {
        return oldVersion;
    }

    public Version getNewVersion() {
        return newVersion;
    }

    public String getComponentUuid() {
        return componentUuid;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public String toString() {
        return "ModuleUpdateEntity{" +
                "oldVersion=" + oldVersion +
                ", newVersion=" + newVersion +
                ", componentUuid='" + componentUuid + '\'' +
                '}';
    }
}
