package com.fuzzy.main.platform.querypool.service.utils;

import com.fuzzy.main.platform.querypool.QueryPool;

import java.util.Map;
import java.util.StringJoiner;

public class QueryPoolUtils {

    public static String toStringResources(Map<String, QueryPool.LockType> resources) {
        StringJoiner exclusive = new StringJoiner(", ");
        StringJoiner shared = new StringJoiner(", ");
        for (Map.Entry<String, QueryPool.LockType> entry : resources.entrySet()) {
            String clazzName = entry.getKey().substring(entry.getKey().lastIndexOf('.') + 1);
            switch (entry.getValue()) {
                case EXCLUSIVE:
                    exclusive.add(clazzName);
                    break;
                case SHARED:
                    shared.add(clazzName);
                    break;
                default:
                    throw new RuntimeException("Unknown type: " + entry.getValue());
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("{ ");
        if (exclusive.length() > 0) {
            out.append("exclusive: [").append(exclusive.toString()).append(']');
        }
        if (shared.length() > 0) {
            if (exclusive.length() > 0) {
                out.append(", ");
            }
            out.append("shared: [").append(shared.toString()).append(']');
        }
        out.append('}');
        return out.toString();
    }

    public static String toStringStackTrace(Thread thread) {
        StringJoiner out = new StringJoiner(" ", "[", "]");
        for (StackTraceElement item: thread.getStackTrace()) {
            out.add(item.toString());
        }
        return out.toString();
    }
}
