package com.fuzzy.utils;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fuzzy.main.platform.querypool.QueryPool;
import com.fuzzy.main.platform.querypool.service.threadcontext.ThreadContext;
import com.fuzzy.main.platform.sdk.context.Context;
import com.fuzzy.subsystems.Subsystems;

/**
 * Created by user on 02.12.2015.
 */
public class LogbackContext extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {

        Context context = getCurrentContext();
        if (context == null) {
            return "X";
        } else {
            return context.getTrace();
        }
    }

    private static ThreadContext _threadContext = null;

    private static Context getCurrentContext() {
        try {
            if (_threadContext == null) {
                Subsystems subsystems = Subsystems.getInstance();
                if (subsystems == null) return null;

                QueryPool queryPool = subsystems.getQueryPool();
                if (queryPool == null) return null;

                _threadContext = queryPool.getThreadContext();
            }

            return _threadContext.getContext();
        } catch (Throwable ignore) {
            return null;
        }
    }

}
