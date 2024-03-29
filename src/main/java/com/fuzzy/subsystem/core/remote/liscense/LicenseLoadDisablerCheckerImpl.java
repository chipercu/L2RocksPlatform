package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

import java.util.Collection;

public class LicenseLoadDisablerCheckerImpl implements LicenseLoadDisablerChecker {
    private final Collection<RCLicenseLoadDisabler> licenseLoadDisablerCollection;

    public LicenseLoadDisablerCheckerImpl(CoreSubsystem component) {
        licenseLoadDisablerCollection = component.getRemotes().getControllers(RCLicenseLoadDisabler.class);
    }

    @Override
    public void checkLicenseLoadDisabled() throws PlatformException {
        for (RCLicenseLoadDisabler disabler : licenseLoadDisablerCollection) {
            if (disabler.isLicenseLoadDisabled()) {
                throw CoreExceptionBuilder.buildLicenseLoadDisabledException();
            }
        }
    }
}
