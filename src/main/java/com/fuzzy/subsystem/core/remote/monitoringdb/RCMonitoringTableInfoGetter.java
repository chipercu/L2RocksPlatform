package com.fuzzy.subsystem.core.remote.monitoringdb;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public interface RCMonitoringTableInfoGetter extends RController {

    @Nullable MonitoringTableInfo getActivityTableInfo() throws PlatformException;

    @Nullable MonitoringTableInfo getAgentLogTableInfo() throws PlatformException;

    ArrayList<MonitoringTableInfo> getTableInfos() throws PlatformException;

}
