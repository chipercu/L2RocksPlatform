package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.license.LicenseBuilder;
import com.fuzzy.subsystem.core.domainobject.license.LicenseReadable;

public interface RCLicense extends QueryRemoteController {

    LicenseReadable create(LicenseBuilder builder, ContextTransaction context) throws PlatformException;

    Long remove(long licenseId, ContextTransaction context) throws PlatformException;
}
