package com.fuzzy.subsystem.core.remote.systemevent;

import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;

public interface RControllerSystemEvent extends RController {

    void captureEvent(SystemEvent systemEvent) throws PlatformException;
}