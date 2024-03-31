package com.fuzzy.platform.component.database.remote.cfconfig;

import com.fuzzy.platform.component.database.DatabaseComponent;
import com.fuzzy.platform.component.database.remote.cfconfig.RControllerColumnFamilyConfig;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.Collection;
import java.util.HashMap;

public class ColumnFamilyConfigService {

    private final Collection<RControllerColumnFamilyConfig> rControllerColumnFamilyConfigs;

    public ColumnFamilyConfigService(DatabaseComponent databaseComponent) {
        rControllerColumnFamilyConfigs = databaseComponent.getRemotes().getControllers(RControllerColumnFamilyConfig.class);
    }

    public HashMap<String, ColumnFamilyConfig> getConfigs() throws PlatformException {
        final HashMap<String, ColumnFamilyConfig> columnConfigs = new HashMap<>();
        for (RControllerColumnFamilyConfig controller : rControllerColumnFamilyConfigs) {
            columnConfigs.putAll(controller.getConfiguredColumns());
        }
        return columnConfigs;
    }
}