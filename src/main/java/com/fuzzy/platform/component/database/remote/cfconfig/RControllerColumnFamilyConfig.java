package com.fuzzy.platform.component.database.remote.cfconfig;

import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.HashMap;

public interface RControllerColumnFamilyConfig extends RController {
    HashMap<String, ColumnFamilyConfig> getConfiguredColumns() throws PlatformException;
}