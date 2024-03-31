package com.fuzzy.subsystem.entityprovidersdk.entity;

import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityClass {

    String uuid() default "";

    String name() default "";

    Class<? extends DataSourceProvider> dataSource() default DataSourceProvider.class;
}
