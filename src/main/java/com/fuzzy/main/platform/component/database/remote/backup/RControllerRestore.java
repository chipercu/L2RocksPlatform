package com.fuzzy.main.platform.component.database.remote.backup;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;

public interface RControllerRestore extends QueryRemoteController {

    void restoreBackup(String backupDirPath, String backupName, String newDbPath) throws PlatformException;
}
