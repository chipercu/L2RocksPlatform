package com.fuzzy.subsystem.extensions.multilang;

import java.io.File;
import java.io.FilenameFilter;

public class PropertiesFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".properties");
    }
}
