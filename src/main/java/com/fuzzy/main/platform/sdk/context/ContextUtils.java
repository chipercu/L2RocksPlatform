package com.fuzzy.main.platform.sdk.context;

public class ContextUtils {

    public static String toTrace(Context context) {
        return "(trace: " + context.getTrace() + ")";
    }
}
