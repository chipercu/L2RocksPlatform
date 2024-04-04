package com.fuzzy.config.configsystem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {

    String desc();
//    Class<?> type();
    String strValue() default "";
    String[] strArrValue() default {};
    int intValue() default 0;
    int[] intArrValue() default {};
    long longValue() default 0;
    long[] longArrValue() default {};
    double doubleValue() default 0;
    double[] doubleArrValue() default {};
    boolean boolValue() default false;
    boolean[] boolArrValue() default {};
}
