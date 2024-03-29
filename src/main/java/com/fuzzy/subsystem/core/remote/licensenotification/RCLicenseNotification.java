package com.fuzzy.subsystem.core.remote.licensenotification;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCLicenseNotification extends QueryRemoteController {

    void onBeforeRemoveLicense(Long licenseId, ContextTransaction context) throws PlatformException;

    void onBeforeExpirationLicense(ContextTransaction context) throws PlatformException;

}
