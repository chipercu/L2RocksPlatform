package com.fuzzy.database.anotation;

import com.fuzzy.database.anotation.*;
import com.fuzzy.database.anotation.Field;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {

    String namespace();
    String name();
    Field[] fields();

    HashIndex[] hashIndexes() default {};
    PrefixIndex[] prefixIndexes() default {};
    IntervalIndex[] intervalIndexes() default {};
    RangeIndex[] rangeIndexes() default {};
}
