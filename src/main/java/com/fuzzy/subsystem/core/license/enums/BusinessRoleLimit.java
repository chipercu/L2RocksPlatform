package com.fuzzy.subsystem.core.license.enums;

import com.fuzzy.database.utils.BaseEnum;

public enum BusinessRoleLimit implements BaseEnum {

    BUSINESS_USER(1, "business_user"),
    ANALYST(2, "analyst"),
    ADMIN(3, "admin");

    private final int id;
    private final String key;

    BusinessRoleLimit(int id, String key) {
        this.id = id;
        this.key = key;
    }

    @Override
    public int intValue() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public static BusinessRoleLimit getById(int id) {
        for (BusinessRoleLimit value : BusinessRoleLimit.values()) {
            if (value.intValue() == id) {
                return value;
            }
        }
        return null;
    }

    public static BusinessRoleLimit ofKey(String key) {
        for (BusinessRoleLimit businessRoleLimit : BusinessRoleLimit.values()) {
            if (businessRoleLimit.getKey().equals(key)) {
                return businessRoleLimit;
            }
        }
        return null;
    }
}
