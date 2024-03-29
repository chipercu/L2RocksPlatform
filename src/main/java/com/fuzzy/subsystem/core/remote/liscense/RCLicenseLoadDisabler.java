package com.fuzzy.subsystem.core.remote.liscense;

import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.platform.exception.PlatformException;

public interface RCLicenseLoadDisabler extends RController {

    boolean isLicenseLoadDisabled() throws PlatformException;
}
