package com.fuzzy.platform.component.database.remote.backup;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;

public interface RControllerRestore extends QueryRemoteController {

    void restoreBackup(String backupDirPath, String backupName, String newDbPath) throws PlatformException;
}
