package com.fuzzy.platform.sdk.component.version;

import com.fuzzy.platform.sdk.component.version.Version;

//https://semver.org/lang/ru/
public class CompatibleVersion {

    private final com.fuzzy.platform.sdk.component.version.Version minimum;
    private final com.fuzzy.platform.sdk.component.version.Version target;

    public CompatibleVersion(com.fuzzy.platform.sdk.component.version.Version minimum, com.fuzzy.platform.sdk.component.version.Version target) {
        if (com.fuzzy.platform.sdk.component.version.Version.compare(minimum, target) > 0) {
            minimum = target;
        }

        this.minimum = minimum;
        this.target = target;
    }

    public CompatibleVersion(com.fuzzy.platform.sdk.component.version.Version target) {
        this(new com.fuzzy.platform.sdk.component.version.Version(target.product, target.major, target.minor, 0), target);
    }

    public com.fuzzy.platform.sdk.component.version.Version getMinimum() {
        return minimum;
    }

    public com.fuzzy.platform.sdk.component.version.Version getTarget() {
        return target;
    }

    public boolean isCompatibleWith(com.fuzzy.platform.sdk.component.version.Version version) {
        final int minCmpRes = com.fuzzy.platform.sdk.component.version.Version.compare(version, minimum);
        if (minCmpRes <= 0) {
            return minCmpRes == 0;
        }

        final int trgCmpRes = Version.compare(version, target);
        if (trgCmpRes <= 0) {
            return true;
        }

        if (version.product != target.product) {
            return false;
        }
        if (version.major != target.major) {
            return false;
        }
        return version.minor >= target.minor;
    }
}
