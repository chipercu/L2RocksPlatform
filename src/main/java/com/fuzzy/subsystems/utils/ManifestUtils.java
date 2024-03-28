package com.fuzzy.subsystems.utils;

import com.fuzzy.main.platform.sdk.component.version.Version;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ManifestUtils {

    private final static Logger log = LoggerFactory.getLogger(ManifestUtils.class);

    public static final String MANIFEST_FILENAME = "manifest.json";
    public static final String ICON_FILENAME = "icon.png";
    public static final String ICON_EXTENSION = "png";
    public static final String SDK_NAME = "sdk";

    public static final String JSON_PROP_NAME = "name";
    public static final String JSON_PROP_DISPLAY_NAME = "display_name";
    public static final String JSON_PROP_VERSION = "version";
    public static final String JSON_PROP_BUILD = "build";

    public static Version readSdkVersion() throws IOException, ParseException {
        JSONObject object = parseManifest(SDK_NAME);
        return Version.parse(object.getAsString(JSON_PROP_VERSION));
    }

    public static String readBuildVersion() throws IOException, ParseException {
        JSONObject object = parseManifest(SDK_NAME);
        return BuildVersionUtils.readBuildVersion(object.getAsString(JSON_PROP_BUILD));
    }

    public static JSONObject parseManifest(String moduleName) throws IOException, ParseException {
        Module module;
        if ("sdk".equals(moduleName)) {
            module = ManifestUtils.class.getModule();
        } else {
            module = ModuleLayer.boot().findModule(moduleName).orElse(ManifestUtils.class.getModule());
        }

        String pathFileManifest = moduleName.replaceAll("\\.", "/") + '/' + MANIFEST_FILENAME;
        InputStream stream = module.getResourceAsStream(pathFileManifest);
        if (stream == null) {
            throw new RuntimeException("Not found file manifest: " + pathFileManifest);
        }
        InputStream inputStream = stream;
        try (inputStream) {
            return parseManifest(inputStream);
        }
    }

    public static JSONObject parseManifest(InputStream manifest) throws IOException, ParseException {
        return (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(manifest);
    }
}
