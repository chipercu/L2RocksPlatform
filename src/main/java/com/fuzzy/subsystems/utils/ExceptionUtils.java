package com.fuzzy.subsystems.utils;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.exception.runtime.PlatformRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils {

    public static RuntimeException coercionRuntimeException(Throwable throwable) {
        if (throwable instanceof PlatformException) {
            throw new PlatformRuntimeException((PlatformException) throwable);
        } else if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }

    public static void rethrow(Throwable e) throws Exception {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof Exception) {
            throw (Exception) e;
        } else {
            throw new RuntimeException(e);
        }
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        final List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }
}
