package com.fuzzy.platform.component.database.remote.backup;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;

public interface RControllerBackup extends QueryRemoteController {

    void createBackup(String backupDirPath, String backupName) throws PlatformException;
}
