package com.fuzzy.main.cluster.graphql.schema.datafetcher.utils;

/**
 * Created by kris on 01.02.17.
 */
public class ExtResult {

    public static Object get(Object result) {
        if (result == null) {
            return null;
        } else if (result.getClass().isEnum()) {
            return ((Enum) result).name();
        } else {
            return result;
        }
    }
}
