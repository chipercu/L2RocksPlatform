package com.fuzzy.subsystems.subsystem;

import com.fuzzy.cluster.struct.Component;
import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.sdk.component.version.CompatibleVersion;

public class SdkInfoBuilder {

    private final Info.Builder builder;

    public SdkInfoBuilder(String uuid, Class<? extends Subsystem> clazz) {
        this.builder = new Info.Builder(uuid, Subsystems.VERSION)
                .withPlatform(true)
                .withComponentClass(clazz)
                .withSdkVersion(new CompatibleVersion(Subsystems.VERSION));
    }

    public SdkInfoBuilder withDependence(Class<? extends Component> dependence) {
        builder.withDependence(dependence);
        return this;
    }

    public SdkInfoBuilder withDependence(Class<? extends Subsystem> dependence, CompatibleVersion version) {
        builder.withDependence(dependence, version);
        return this;
    }

    public SdkInfoBuilder withSdkDependence(Info subsystemInfo) {
        return withDependence(subsystemInfo.getComponent(), new CompatibleVersion(subsystemInfo.getVersion()));
    }

    public Info build() {
        return builder.build();
    }
}