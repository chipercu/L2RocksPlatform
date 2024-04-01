package com.fuzzy.subsystem.debug.benchmark;

import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;

public class Geodata {


    public static void main(String[] args) throws Exception {
        common.init();
        GeoEngine.LoadGeodataFile((byte) 16, (byte) 19);
        common.PromptEnterToContinue();
    }
}