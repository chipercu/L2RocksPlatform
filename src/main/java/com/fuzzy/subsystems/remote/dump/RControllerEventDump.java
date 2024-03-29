package com.fuzzy.subsystems.remote.dump;

import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.platform.exception.PlatformException;

import java.net.URI;

public interface RControllerEventDump extends RController {

    void action(URI dump) throws PlatformException;

}
