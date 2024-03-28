package com.fuzzy.main.cluster.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
public @interface Info {

    String uuid();

    Class<? extends com.fuzzy.main.cluster.struct.Component>[] dependencies() default {};
}
