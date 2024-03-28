package com.fuzzy.subsystems.remote.dump;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;

import java.net.URI;

public interface RControllerEventDump extends RController {

    void action(URI dump) throws PlatformException;

}
