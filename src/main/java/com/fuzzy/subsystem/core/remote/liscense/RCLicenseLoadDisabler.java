package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.platform.exception.PlatformException;

public interface RCLicenseLoadDisabler extends RController {

    boolean isLicenseLoadDisabled() throws PlatformException;
}
