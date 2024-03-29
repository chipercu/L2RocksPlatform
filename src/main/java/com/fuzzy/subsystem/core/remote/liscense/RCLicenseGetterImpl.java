package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.main.cluster.core.remote.AbstractRController;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.LicenseManager;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;

import java.time.Instant;

public class RCLicenseGetterImpl extends AbstractRController<CoreSubsystem> implements RCLicenseGetter {


    public RCLicenseGetterImpl(CoreSubsystem component) {
        super(component);
    }

    @Override
    public long getModuleParameterLimit(String moduleUUID, LicenseParameter licenseParameter) throws PlatformException {
        LicenseManager.CommonLicense commonLicense = component.getCommonLicense();
        return commonLicense == null ? 0L : commonLicense.getParameterLimit(moduleUUID, licenseParameter);
    }

    @Override
    public long getBusinessRoleLimit(BusinessRoleLimit businessRoleLimit) throws PlatformException {
        LicenseManager.CommonLicense commonLicense = component.getCommonLicense();
        return commonLicense == null ? 0L : commonLicense.getBusinessRoleLimit(businessRoleLimit);
    }

    @Override
    public Instant getNearestExpirationTime() throws PlatformException {
        LicenseManager.CommonLicense commonLicense = component.getCommonLicense();
        if (commonLicense == null) {
            throw CoreExceptionBuilder.buildLicenseIsExpiredException();
        }
        return commonLicense.getExpirationTime();
    }

    @Override
    public boolean hasActualLicense() throws PlatformException {
        LicenseManager.CommonLicense commonLicense = component.getCommonLicense();
        return commonLicense != null && commonLicense.getExpirationTime().isAfter(Instant.now());
    }

    @Override
    public Instant getNextParameterResetDate(String moduleUUID, LicenseParameter licenseParameter) throws PlatformException {
        LicenseManager.CommonLicense commonLicense = component.getCommonLicense();
        return commonLicense == null ? null : commonLicense.getNextResetDate(moduleUUID, licenseParameter);
    }

    @Override
    public void checkLicenseParameterRestrictions(String moduleUUID, LicenseParameter licenseParameter, long currentState) throws PlatformException {
        LicenseManager.CommonLicense commonLicense = component.getCommonLicense();
        long parameterLimit = commonLicense == null ? 0 : commonLicense.getParameterLimit(moduleUUID, licenseParameter);
        if (parameterLimit == -1L) {
            return;
        }
        switch (licenseParameter) {
            case MIN_SCRIPT_RUN_PERIOD -> {
                if (parameterLimit == 0
                        || Instant.ofEpochMilli(currentState).plusMillis(parameterLimit).isAfter(Instant.now())) {
                    throw CoreExceptionBuilder.buildLicenseRestrictionException(licenseParameter.getKey());
                }
            }
            case DISK_SPACE -> {// проверка после превышения
                if (currentState >= parameterLimit) {
                    throw CoreExceptionBuilder.buildLicenseRestrictionException(licenseParameter.getKey());
                }
            }
            case USERS_WITH_ROLE -> {
                if (parameterLimit == 0 && currentState == 1) { //Необходимо для возможности создания администратора при инициализации системы
                    return;
                }
                if (currentState > parameterLimit) {
                    throw CoreExceptionBuilder.buildLicenseRestrictionException(licenseParameter.getKey());
                }
            }
            default -> { //стандартная проверка до факта превышения
                if (currentState > parameterLimit) {
                    throw CoreExceptionBuilder.buildLicenseRestrictionException(licenseParameter.getKey());
                }
            }
        }
    }
}
