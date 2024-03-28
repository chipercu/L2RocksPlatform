package com.fuzzy.main.platform.component.database.remote.cfconfig;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.HashMap;

public interface RControllerColumnFamilyConfig extends RController {
    HashMap<String, ColumnFamilyConfig> getConfiguredColumns() throws PlatformException;
}