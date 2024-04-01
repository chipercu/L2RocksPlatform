package com.fuzzy.subsystem.debug.benchmark;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;

public class GeoCheckOptimized {
    public static void main(String[] args) throws Exception {
        common.init();
        ConfigValue.GeoFilesPattern = "(\\d{2}_\\d{2})\\.l2j";
        ConfigValue.AllowDoors = false;
        ConfigValue.CompactGeoData = true;
        GeoEngine.load();
        common.PromptEnterToContinue();
    }
}