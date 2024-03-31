package com.fuzzy.database.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by kris on 18.05.17.
 */
@Target({})
@Retention(RUNTIME)
public @interface HashIndex {

    int[] fields();
}