package com.fuzzy.subsystem.core.remote.monitoringdb;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;

public interface RControllerMonitoringDBInfoGetter extends QueryRemoteController {

    String getActivityDBName() throws PlatformException;
    String getActivityTableName() throws PlatformException;
    String getAppCrashInfoDbName() throws PlatformException;
    String getAppCrashInfoTableName() throws PlatformException;
    String getFilesystemActivityDbName() throws PlatformException;
    String getFilesystemActivityTableName() throws PlatformException;
}
