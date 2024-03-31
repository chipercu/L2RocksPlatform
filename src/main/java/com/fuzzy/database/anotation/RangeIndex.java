package com.fuzzy.database.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface RangeIndex {

    /**
     * Supported Long, Instant and Double types only.
     */
    int beginField();
    int endField();

    int[] hashedFields() default {};
}
