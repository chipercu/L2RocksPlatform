package com.fuzzy.subsystem.core.remote.monitoringdb;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;

public interface RControllerMonitoringDBInfoGetter extends QueryRemoteController {

    String getActivityDBName() throws PlatformException;
    String getActivityTableName() throws PlatformException;
    String getAppCrashInfoDbName() throws PlatformException;
    String getAppCrashInfoTableName() throws PlatformException;
    String getFilesystemActivityDbName() throws PlatformException;
    String getFilesystemActivityTableName() throws PlatformException;
}
