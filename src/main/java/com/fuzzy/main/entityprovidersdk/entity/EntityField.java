package com.fuzzy.main.entityprovidersdk.entity;

import com.fuzzy.main.entityprovidersdk.enums.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityField {

    DataType type() default DataType.STRING;

    String name() default "column_name";
}
