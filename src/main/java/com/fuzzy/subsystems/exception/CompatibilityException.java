package com.fuzzy.subsystems.exception;

import com.fuzzy.cluster.exception.ClusterException;
import com.fuzzy.platform.sdk.component.version.Version;
import com.fuzzy.subsystems.subsystem.Subsystem;

public class CompatibilityException extends ClusterException {

    public CompatibilityException(Class controllableClass, Version controllableVer, Class actualClass, Version actualVer) {
        super(toString(controllableClass, controllableVer) + " is incompatible with the " + toString(actualClass, actualVer));
    }

    public CompatibilityException(Subsystem controllable, Class actualClass, Version actualVer) {
        this(controllable.getClass(), controllable.getInfo().getVersion(), actualClass, actualVer);
    }

    private static String toString(Class clazz, Version ver) {
        return clazz.getSimpleName() + " (ver. " + ver + ")";
    }
}
