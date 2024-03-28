package com.fuzzy.main.cluster.graphql.utils;

public class CaseFormat {

    public static String toLowerUnderscore(String value) {
        String result = value.replaceAll("([a-z])([A-Z])", "$1_$2");
        return result.toLowerCase();
    }
}
