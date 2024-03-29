package com.fuzzy.main.entityprovidersdk.entity.validation;

public interface Validator<T> {
    boolean validate(T source);
}
