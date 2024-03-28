package com.fuzzy.main.platform.sdk.component.version;

//https://semver.org/lang/ru/
public class CompatibleVersion {

    private final Version minimum;
    private final Version target;

    public CompatibleVersion(Version minimum, Version target) {
        if (Version.compare(minimum, target) > 0) {
            minimum = target;
        }

        this.minimum = minimum;
        this.target = target;
    }

    public CompatibleVersion(Version target) {
        this(new Version(target.product, target.major, target.minor, 0), target);
    }

    public Version getMinimum() {
        return minimum;
    }

    public Version getTarget() {
        return target;
    }

    public boolean isCompatibleWith(Version version) {
        final int minCmpRes = Version.compare(version, minimum);
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
