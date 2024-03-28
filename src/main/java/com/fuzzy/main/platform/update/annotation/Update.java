package com.fuzzy.main.platform.update.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Update {

    String componentUUID();
    String version();
    String previousVersion();
    Dependency[] dependencies() default {};
}
