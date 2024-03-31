package com.fuzzy.platform.update.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface Dependency {

    String uuid();
    String version() default "";
    boolean optional() default false;
}
