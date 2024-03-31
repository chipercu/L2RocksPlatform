package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;

public interface RCLicenseParameterReset extends QueryRemoteController {

    void resetLicenseParameterCurrentState(String moduleUUID, LicenseParameter licenseParameter, ContextTransaction context) throws PlatformException;
}
