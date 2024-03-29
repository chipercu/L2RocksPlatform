package com.fuzzy.subsystem.core.exception;

import com.infomaximum.database.utils.BaseEnum;
import com.infomaximum.platform.exception.ExceptionFactory;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.PlatformExceptionFactory;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

import java.util.Collections;
import java.util.HashMap;

public class CoreExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new PlatformExceptionFactory(CoreSubsystemConsts.UUID);

    public static final String EMPLOYEE_SELF_REMOVE_CODE = "employee_self_remove";
    public static final String ADMINISTRATOR_EMPLOYEE_CODE = "administrator_employee";
    public static final String ADMINISTRATOR_ACCESS_ROLE_CODE = "administrator_access_role";
    public static final String ASSIGNED_TO_EMPLOYEES_ACCESS_ROLE_CODE = "assigned_to_employees_access_role";

    public static PlatformException buildAlreadyActiveServerException() {
        return EXCEPTION_FACTORY.build("already_active_server");
    }

    public static PlatformException buildNotActivePlatformException(String subsystemUuid) {
        return EXCEPTION_FACTORY.build(
                "not_active_subsystem",
                Collections.singletonMap("subsystem_uuid", subsystemUuid)
        );
    }

    public static PlatformException buildLastAdministratorException() {
        return EXCEPTION_FACTORY.build("last_administrator");
    }

    public static PlatformException buildEmployeeSelfMergerException(Long employeeId) {
        return EXCEPTION_FACTORY.build(
                "employee_self_merger",
                Collections.singletonMap("employee_id", employeeId)
        );
    }

    public static PlatformException buildNewPasswordEqualsCurrentPasswordException() {
        return EXCEPTION_FACTORY.build("new_password_equals_current_password");
    }

    public static PlatformException buildNewPasswordEqualsUsedPasswordException() {
        return EXCEPTION_FACTORY.build("new_password_equals_used_password");
    }

    public static PlatformException buildEmptyEmailException() {
        return EXCEPTION_FACTORY.build("empty_email");
    }

    public static PlatformException buildRequireAllEmployeeAccessException() {
        return EXCEPTION_FACTORY.build("require_all_employee_access");
    }

    public static PlatformException buildUnsupportedApiKeyTypeException() {
        return EXCEPTION_FACTORY.build("unsupported_api_key_type_exception");
    }

    public static PlatformException buildReadOnlyTagException(long tagId) {
        return EXCEPTION_FACTORY.build("read_only_tag", new HashMap<String, Object>() {{
            put("tag_id", tagId);
        }});
    }

    public static PlatformException buildInvalidLicenseException(Throwable e) {
        return EXCEPTION_FACTORY.build("invalid_license", e);
    }

    public static PlatformException buildInvalidLicenseFormatException() {
        return EXCEPTION_FACTORY.build("invalid_license_format");
    }

    public static PlatformException buildInvalidLicenseVersionException() {
        return EXCEPTION_FACTORY.build("invalid_license_version");
    }

    public static PlatformException buildLicenseIsExpiredException() {
        return EXCEPTION_FACTORY.build("license_is_expired");
    }

    public static PlatformException buildEmptyFieldObjectException() {
        return EXCEPTION_FACTORY.build("empty_field_object");
    }

    public static PlatformException buildEmptyFieldNameException() {
        return EXCEPTION_FACTORY.build("empty_field_name");
    }

    public static PlatformException buildEmptyFieldDataTypeException() {
        return EXCEPTION_FACTORY.build("empty_field_data_type");
    }

    public static PlatformException buildEmptyFieldListSourceTypeException() {
        return EXCEPTION_FACTORY.build("empty_field_list_source_type");
    }

    public static PlatformException buildSynchronizedFieldException() {
        return EXCEPTION_FACTORY.build("synchronized_field");
    }

    public static PlatformException buildInvalidFieldDataTypeException() {
        return EXCEPTION_FACTORY.build("invalid_field_data_type");
    }

    public static PlatformException buildFieldArrayItemNotFoundException() {
        return EXCEPTION_FACTORY.build("field_array_item_not_found");
    }

    public static PlatformException buildInvalidAuthenticationTypeException() {
        return EXCEPTION_FACTORY.build("invalid_authentication_type");
    }

    public static PlatformException buildIntegratedAuthenticationAlreadyExistsException() {
        return EXCEPTION_FACTORY.build("integrated_authentication_already_exists");
    }

    public static PlatformException buildLastAuthenticationException() {
        return EXCEPTION_FACTORY.build("last_authentication");
    }

    public static PlatformException buildUnsupportedSortingColumnException(BaseEnum sortedColumn) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("sorting_column", sortedColumn);
        return EXCEPTION_FACTORY.build("unsupported_sorting_column", params);
    }

    public static PlatformException buildIntegratedAuthenticationNotFoundException() {
        return EXCEPTION_FACTORY.build("integrated_authentication_not_found");
    }

    public static PlatformException buildMonitorMeasurementException(Throwable e) {
        return EXCEPTION_FACTORY.build("monitor_measurement_error_behavior", e);
    }

    public static PlatformException buildLicenseRestrictionException(String parameter) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("parameter", parameter);
        return EXCEPTION_FACTORY.build("license_restriction", params);
    }

    public static PlatformException buildLicenseResetParametersException() {
        return EXCEPTION_FACTORY.build("license_with_reset_perameters_already_exist");
    }

    public static PlatformException buildLicenseLoadDisabledException() {
        return EXCEPTION_FACTORY.build("license_load_disabled");
    }
}
