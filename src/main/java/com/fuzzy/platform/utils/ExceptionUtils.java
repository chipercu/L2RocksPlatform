package com.fuzzy.platform.utils;

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

    /**
     * <p>Returns the list of {@code Throwable} objects in the
     * exception chain.</p>
     *
     * <p>A throwable without cause will return a list containing
     * one element - the input throwable.
     * A throwable with one cause will return a list containing
     * two elements. - the input throwable and the cause throwable.
     * A {@code null} throwable will return a list of size zero.</p>
     *
     * <p>This method handles recursive cause structures that might
     * otherwise cause infinite loops. The cause chain is processed until
     * the end is reached, or until the next item in the chain is already
     * in the result set.</p>
     *
     * @param throwable  the throwable to inspect, may be null
     * @return the list of throwables, never null
     * @since 2.2
     */
    public static List<Throwable> getThrowableList(Throwable throwable) {
        final List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }
}
