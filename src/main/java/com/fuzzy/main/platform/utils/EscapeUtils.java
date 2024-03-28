package com.fuzzy.main.platform.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class EscapeUtils {

    public static String escapeFileNameFromContentDisposition(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
