package com.fuzzy.subsystems.remote;

import com.infomaximum.cluster.core.remote.Remotes;
import com.infomaximum.cluster.core.remote.struct.RController;

import java.util.Collection;

public class RExecutor<T extends RController> extends MultiExecutor<T> {

    public RExecutor(Remotes remotes, Class<T> controllerClass) {
        super(remotes.getControllers(controllerClass));
    }

    public RExecutor(Collection<T> controllers) {
        super(controllers);
    }
}
