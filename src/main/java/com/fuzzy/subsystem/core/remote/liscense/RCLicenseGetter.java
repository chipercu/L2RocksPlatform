package com.fuzzy.subsystem.core.remote.liscense;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;

import java.time.Instant;

public interface RCLicenseGetter extends RController {

    long getModuleParameterLimit(String moduleUUID, LicenseParameter licenseParameter) throws PlatformException;

    long getBusinessRoleLimit(BusinessRoleLimit businessRoleLimit) throws PlatformException;

    Instant getNearestExpirationTime() throws PlatformException;

    boolean hasActualLicense() throws PlatformException;

    Instant getNextParameterResetDate(String moduleUUID, LicenseParameter licenseParameter) throws PlatformException;

    /**
     * Проверка лицензионного параметра licenseParameter по его текущему состоянию currentState.
     *
     * @param moduleUUID       Идентификатор модуля, функционал которого лицензирован.
     * @param licenseParameter Параметр, по которому проводится проверка
     *                         {@link com.infomaximum.subsystem.core.license.enums.LicenseParameter}
     * @param currentState     Текущее состояние проверяемого параметра.
     *                         Для {@link LicenseParameter} MIN_SCRIPT_RUN_PERIOD и CLICKHOUSE_SQL_EXECUTION_TIMEOUT
     *                         в качестве currentState указывается время последнего выполнения в миллисекундах.
     * @throws PlatformException В случае срабатывания лицензионного ограничения
     */
    void checkLicenseParameterRestrictions(String moduleUUID, LicenseParameter licenseParameter, long currentState) throws PlatformException;
}
