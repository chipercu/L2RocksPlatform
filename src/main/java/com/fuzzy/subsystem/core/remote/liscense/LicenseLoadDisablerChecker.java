package com.fuzzy.subsystem.core.remote.liscense;

import com.infomaximum.platform.exception.PlatformException;

public interface LicenseLoadDisablerChecker {

    void checkLicenseLoadDisabled() throws PlatformException;

}
