package com.fuzzy.subsystem.core.license.updater;

import com.fuzzy.main.platform.exception.PlatformException;
import net.minidev.json.JSONObject;

public interface LicenseSchemeChainUpdater {

    void updateToActual(JSONObject licenseJson) throws PlatformException;
}
