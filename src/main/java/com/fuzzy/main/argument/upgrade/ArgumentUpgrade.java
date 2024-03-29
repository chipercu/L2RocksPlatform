package com.fuzzy.main.argument.upgrade;

import com.google.common.base.Strings;
import com.infomaximum.platform.sdk.component.version.Version;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

public class ArgumentUpgrade {

    public static class UpdateModule {

        public final String uuid;
        public final Version oldVersion;
        public final Version newVersion;

        public UpdateModule(String uuid, Version oldVersion, Version newVersion) {
            if (Strings.isNullOrEmpty(uuid)) {
                throw new IllegalArgumentException();
            }
            this.uuid = uuid;
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }
    }

    public final List<UpdateModule> updateModules;

    private ArgumentUpgrade(List<UpdateModule> updateModules) {
        this.updateModules = Collections.unmodifiableList(updateModules);
    }

    public static ArgumentUpgrade build(String value) {
        try {
            JSONObject upgrades = (JSONObject) new JSONParser(DEFAULT_PERMISSIVE_MODE).parse(value);

            List<UpdateModule> updateModules = ((JSONArray) upgrades.get("update"))
                    .stream()
                    .map(o -> (JSONObject) o)
                    .map(j -> new UpdateModule(
                            j.getAsString("uuid"),
                            Version.parse(j.getAsString("old_version")),
                            Version.parse(j.getAsString("new_version"))
                    ))
                    .collect(Collectors.toList());

            return new ArgumentUpgrade(updateModules);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
