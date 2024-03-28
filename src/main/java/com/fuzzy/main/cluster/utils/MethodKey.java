package com.fuzzy.main.cluster.utils;

import java.lang.reflect.Method;

public class MethodKey {

    public static int calcMethodKey(Method method) {
        int methodKey = method.getName().hashCode();
        for (Class parameterType : method.getParameterTypes()) {
            methodKey ^= parameterType.getName().hashCode();
        }
        return methodKey;
    }
}
