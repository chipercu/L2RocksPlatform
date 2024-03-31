package com.fuzzy.subsystem.entityprovidersdk.entity.validation;

public interface Validator<T> {
    boolean validate(T source);
}
