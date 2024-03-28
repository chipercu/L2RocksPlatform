package com.fuzzy.main.rdao.database.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by user on 19.04.2017.
 */
@Target({})
@Retention(RUNTIME)
public @interface Field {

    int number();
    String name();
    Class type();
    Class packerType() default Class.class;

    Class foreignDependency() default Class.class;
}