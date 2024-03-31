package com.fuzzy.config.configsystem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

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
