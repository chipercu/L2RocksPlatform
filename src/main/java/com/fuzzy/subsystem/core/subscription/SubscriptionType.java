package com.fuzzy.subsystem.core.subscription;

public enum SubscriptionType {

    EMPLOYEE_UPDATE(1);

    private int id;

    SubscriptionType(int id) {
        this.id = id;
    }

    public int intValue() {
        return id;
    }
}
