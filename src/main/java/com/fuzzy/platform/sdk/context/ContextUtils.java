package com.fuzzy.platform.sdk.context;

import com.fuzzy.platform.sdk.context.Context;

public class ContextUtils {

    public static String toTrace(Context context) {
        return "(trace: " + context.getTrace() + ")";
    }
}
