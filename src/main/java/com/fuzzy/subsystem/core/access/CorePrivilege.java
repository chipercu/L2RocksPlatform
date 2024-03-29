package com.fuzzy.subsystem.core.access;

import com.fuzzy.subsystems.access.PrivilegeEnum;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.Privilege;


public enum CorePrivilege implements PrivilegeEnum {

    // Привилегия «Общие настройки»
    GENERAL_SETTINGS(1, "core_privilege.general_settings",
            AccessOperation.READ, AccessOperation.WRITE),

    // Привилегия «Ключи API»
    API_KEYS(3, "core_privilege.api_keys",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.CREATE, AccessOperation.DELETE),

    // Привилегия «Аутентификация»
    AUTHENTICATION(4, "core_privilege.authentication",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.CREATE, AccessOperation.DELETE),

    // Привилегия «Пользователи и отделы»
    EMPLOYEES(5, "core_privilege.employee",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.CREATE, AccessOperation.DELETE),

    // Привилегия «Доступы пользователей»
    EMPLOYEE_ACCESS(6, "core_privilege.employee_access",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.EXECUTE),

    // Привилегия «Поля пользователя»
    EMPLOYEE_FIELDS(7, "core_privilege.employee_fields",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.CREATE, AccessOperation.DELETE),

    // Привилегия «Роли доступа»
    ACCESS_ROLE(8, "core_privilege.access_role",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.CREATE, AccessOperation.DELETE),

    // Привилегия «Личные настройки»
    PRIVATE_SETTINGS(9, "core_privilege.private_settings",
            AccessOperation.WRITE),

    // Привилегия «Инструмент GraphQL»
    GRAPHQL_TOOL(10, "core_privilege.graphql_tool",
            AccessOperation.EXECUTE),

    // Привилегия «Настройка тегов»
    TAG_SETTINGS(11, "core_privilege.tag_settings",
            AccessOperation.READ, AccessOperation.WRITE, AccessOperation.CREATE, AccessOperation.DELETE),

    // Привилегия «Сервисный режим»
    SERVICE_MODE(12, "core_privilege.service_mode",
            AccessOperation.READ),

    // Привилегия «Право ввода ключа активации»
    LICENSE_KEY_INSERTION(13, "core_privilege.license_key_insertion",
            AccessOperation.EXECUTE);

    private final Privilege privilege;

    CorePrivilege(int id, String key, AccessOperation... availableOperations) {
        this.privilege = new Privilege(id, key, availableOperations);
    }

    public int intValue() {
        return privilege.intValue();
    }

    public String getUniqueKey() {
        return privilege.getUniqueKey();
    }

    public AccessOperationCollection getAvailableOperations() {
        return privilege.getAvailableOperations();
    }

    public static CorePrivilege valueOf(int value) {
        for (CorePrivilege privilege : CorePrivilege.values()) {
            if (privilege.intValue() == value) {
                return privilege;
            }
        }
        return null;
    }

    public static CorePrivilege ofKey(String key) {
        for (CorePrivilege privilege : CorePrivilege.values()) {
            if (privilege.getUniqueKey().equals(key)) {
                return privilege;
            }
        }
        return null;
    }

    public static PrivilegeValue[] getAdminPrivileges() {
        PrivilegeValue[] privilegeValues = new PrivilegeValue[CorePrivilege.values().length];
        int i = 0;
        for (CorePrivilege privilege : CorePrivilege.values()) {
            privilegeValues[i++] = new PrivilegeValue(
                    privilege.getUniqueKey(), privilege.getAvailableOperations());
        }
        return privilegeValues;
    }
}
