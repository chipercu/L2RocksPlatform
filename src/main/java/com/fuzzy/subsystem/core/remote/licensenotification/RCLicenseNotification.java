package com.fuzzy.subsystem.core.remote.licensenotification;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCLicenseNotification extends QueryRemoteController {

    void onBeforeRemoveLicense(Long licenseId, ContextTransaction context) throws PlatformException;

    void onBeforeExpirationLicense(ContextTransaction context) throws PlatformException;

}
