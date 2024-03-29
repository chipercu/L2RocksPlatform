package com.fuzzy.subsystem.core.remote.liscense;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;

public interface RCLicenseParameterReset extends QueryRemoteController {

    void resetLicenseParameterCurrentState(String moduleUUID, LicenseParameter licenseParameter, ContextTransaction context) throws PlatformException;
}
