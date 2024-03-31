package com.fuzzy.platform.querypool.service;

import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.service.utils.QueryPoolUtils;
import com.fuzzy.platform.sdk.context.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class DetectQueueFilling {

    private final static Logger log = LoggerFactory.getLogger(DetectQueueFilling.class);

    private final static long DETECTED_PERIOD = 100;

    public DetectQueueFilling() {
    }

    public void queryComplete(QueryPool.QueryWrapper queryWrapper, Instant timeStart, Instant timeComplete) {
        long duration = timeComplete.toEpochMilli()-timeStart.toEpochMilli();
        if (duration > DETECTED_PERIOD && containsExclusiveLock(queryWrapper.getResources()) ){
            log.warn("Detect query filling {}, duration: {}, resources: {}",
                    ContextUtils.toTrace(queryWrapper.getContext()),
                    duration,
                    QueryPoolUtils.toStringResources(queryWrapper.getResources())
            );
        }
    }

    private static boolean containsExclusiveLock(Map<String, QueryPool.LockType> resources) {
        for (Map.Entry<String, QueryPool.LockType> entry : resources.entrySet()) {
            if (entry.getValue() == QueryPool.LockType.EXCLUSIVE) {
                return true;
            }
        }
        return false;
    }
}
