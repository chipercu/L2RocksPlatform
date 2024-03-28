package com.fuzzy.main.platform.component.database.remote.backup;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;

public interface RControllerBackup extends QueryRemoteController {

    void createBackup(String backupDirPath, String backupName) throws PlatformException;
}
