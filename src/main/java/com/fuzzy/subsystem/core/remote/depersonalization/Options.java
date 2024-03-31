package com.fuzzy.subsystem.core.remote.depersonalization;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public class Options implements RemoteObject {

    private String dbPath;

    public Options(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getDbPath() {
        return dbPath;
    }
}
