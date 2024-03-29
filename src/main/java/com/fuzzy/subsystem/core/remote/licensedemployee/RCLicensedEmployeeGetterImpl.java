package com.fuzzy.subsystem.core.remote.licensedemployee;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.remote.liscense.RCLicenseGetter;
import com.fuzzy.subsystems.remote.RCExecutor;

import java.util.HashSet;

public class RCLicensedEmployeeGetterImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicensedEmployeeGetter {
    private final RCExecutor<RCLicensedEmployee> licensedEmployeeRCExecutor;
    private final RCLicenseGetter rcLicenseGetter;

    public RCLicensedEmployeeGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        licensedEmployeeRCExecutor = new RCExecutor<>(resources, RCLicensedEmployee.class);
        rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
    }

    public HashSet<Long> getLicensedEmployees(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException {
        return licensedEmployeeRCExecutor.apply(rc -> rc.getLicensedEmployees(businessRoleLimit, context), HashSet::new);
    }

    public void validateLicensedEmployeesCount(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException {
        if (businessRoleLimit.equals(BusinessRoleLimit.ADMIN) && !rcLicenseGetter.hasActualLicense()) {
            return; //Необходим как минимум 1 админ, чтобы проинициализировать систему и ввести лицензию.
        }
        long commonBusinessRoleLimit = rcLicenseGetter.getBusinessRoleLimit(businessRoleLimit);
        if (commonBusinessRoleLimit == -1L) {
            return;
        }
        if (getLicensedEmployees(businessRoleLimit, context).size() > commonBusinessRoleLimit) {
            throw CoreExceptionBuilder.buildLicenseRestrictionException(businessRoleLimit.getKey());
        }
    }
}
