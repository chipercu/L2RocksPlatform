package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;

public interface RCLicenseLoadDisabler extends RController {

    boolean isLicenseLoadDisabled() throws PlatformException;
}
