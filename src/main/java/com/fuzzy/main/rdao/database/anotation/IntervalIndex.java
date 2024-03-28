package com.fuzzy.main.rdao.database.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface IntervalIndex {

    /**
     * Supported Long, Double, Instant types only.
     */
    int indexedField();

    int[] hashedFields() default {};
}
