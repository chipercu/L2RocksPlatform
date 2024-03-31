package com.fuzzy.subsystem.core.license.updater.schemeupdater.factory;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.LicenseSchemeUpdater;

public interface SchemeUpdaterFactory {

    LicenseSchemeUpdater getUpdater(int currentSchemeVersion) throws PlatformException;

}
