package com.fuzzy.subsystem.debug.benchmark;

import com.fuzzy.subsystem.config.ConfigSystem;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;

public class GeoMatchesGenerator {
    public static void main(String[] args) throws Exception {
        common.init();
        ConfigSystem.load();
        ConfigValue.GeoFilesPattern = "(\\d{2}_\\d{2})\\.l2j";
        ConfigValue.AllowDoors = false;
        ConfigValue.CompactGeoData = false;
        GeoEngine.load();
        common.log.info("Goedata loaded");
        common.GC();
        GeoEngine.genBlockMatches(0); //TODO
        if (common.YesNoPrompt("Do you want to delete temproary geo checksums files?"))
            GeoEngine.deleteChecksumFiles();
        common.PromptEnterToContinue();
    }
}