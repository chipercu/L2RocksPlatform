package com.fuzzy.subsystem.core.license.updater;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.LicenseSchemeUpdater;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.factory.SchemeUpdaterFactory;
import com.fuzzy.subsystem.core.license.updater.schemeupdater.factory.SchemeUpdaterFactoryImpl;
import net.minidev.json.JSONObject;

import java.util.Objects;

public class LicenseSchemeChainUpdaterImpl implements LicenseSchemeChainUpdater {
    private final int actualVersion;

    public LicenseSchemeChainUpdaterImpl(int actualVersion) {
        this.actualVersion = actualVersion;
    }

    @Override
    public void updateToActual(JSONObject licenseJson) throws PlatformException {
        Number version = licenseJson.getAsNumber("version");
        if (version == null) {
            throw CoreExceptionBuilder.buildInvalidLicenseFormatException();
        }
        int schemeVersion = version.intValue();
        SchemeUpdaterFactory factory = new SchemeUpdaterFactoryImpl();
        while (schemeVersion < actualVersion) {
            LicenseSchemeUpdater updater = factory.getUpdater(schemeVersion);
            if (Objects.isNull(updater)) {
                throw CoreExceptionBuilder.buildInvalidLicenseVersionException();
            }
            schemeVersion = updater.update(licenseJson);
        }
    }

}
