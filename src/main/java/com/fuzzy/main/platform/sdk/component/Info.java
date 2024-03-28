package com.fuzzy.main.platform.sdk.component;

import com.fuzzy.main.platform.sdk.component.version.Version;

public class Info extends com.fuzzy.main.cluster.struct.Info {

    private final Version version;

    protected Info(Builder builder) {
        super(builder);
        this.version = builder.version;
    }

    public Version getVersion() {
        return version;
    }

    public static class Builder<T extends Builder> extends com.fuzzy.main.cluster.struct.Info.Builder<T> {

        private final Version version;

        public Builder(String uuid, Version version) {
            super(uuid);
            this.version = version;
        }

        public Builder(Class<? extends Component> componentClass, Version version) {
            super(componentClass);
            this.version = version;
        }

        @Override
        public Info build() {
            return new Info(this);
        }

    }
}
