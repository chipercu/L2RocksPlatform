package com.fuzzy.subsystem.core.utils;

import com.fuzzy.subsystem.core.config.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;

/**
 * Created by kris on 30.11.16.
 */
public class TimeZoneUtils {

    private static final Logger log = LoggerFactory.getLogger(TimeZoneUtils.class);

    public static String getDisplayName(String id, Language language) {
        return getDisplayName(ZoneId.of(id), language);
    }

    public static String getDisplayName(ZoneId tz, Language language) {
        String zoneDisplayName = tz.getDisplayName(TextStyle.FULL, language.getLocale());
        ZoneOffset zoneOffset = LocalDateTime.now().atZone(tz).getOffset();
        String offsetDisplayName = zoneOffset.getId();
        if (offsetDisplayName.equals("Z")) {
            offsetDisplayName = "+00:00";
        }
        offsetDisplayName = String.format("UTC%s", offsetDisplayName);
        String displayName = zoneDisplayName.equals(offsetDisplayName) ? zoneDisplayName :
                String.format("(%s) %s", offsetDisplayName, zoneDisplayName);
        if (!zoneDisplayName.equals(tz.getId())) {
            displayName = String.format("%s (%s)", displayName, tz.getId());
        }
        return displayName;
    }

    public static ZoneId toZoneIdOrGetDefault(String tz) {
        try {
            return ZoneId.of(tz);
        } catch (DateTimeException e) {
            log.warn("Unsupported timezone: " + tz, e);
        }

        return ZoneId.systemDefault();
    }
}