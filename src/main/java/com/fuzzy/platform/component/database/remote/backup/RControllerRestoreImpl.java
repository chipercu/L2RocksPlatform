package com.fuzzy.platform.component.database.remote.backup;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.component.database.DatabaseComponent;
import com.fuzzy.platform.component.database.exception.DatabaseExceptionBuilder;
import com.fuzzy.platform.component.database.remote.backup.RControllerBackupImpl;
import com.fuzzy.platform.component.database.remote.backup.RControllerRestore;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.exception.NotAbsolutePathException;
import com.fuzzy.rocksdb.backup.RocksDBRestoreBackup;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class RControllerRestoreImpl extends AbstractQueryRController<DatabaseComponent> implements RControllerRestore {

	public RControllerRestoreImpl(DatabaseComponent component, ResourceProvider resources) {
        super(component, resources);
    }

    @Override
    public void restoreBackup(String backupDirPath, String backupName, String newDbPath) throws PlatformException {
        Path dbPath = buildDbPath(newDbPath);
        Path backupPath = RControllerBackupImpl.buildBackupPath(backupDirPath, backupName);
        try {
            RocksDBRestoreBackup.restore(backupPath, dbPath);
        } catch (DatabaseException e) {
            throw DatabaseExceptionBuilder.buildRestoreException(e);
        }
    }

    private static Path buildDbPath(String path) throws PlatformException {
        try {
            Path dbPath = Paths.get(path);
            if (!dbPath.isAbsolute()) {
                throw new NotAbsolutePathException(path);
            }

            if (Files.exists(dbPath)) {
                throw new FileAlreadyExistsException(path);
            }

            return dbPath;
        } catch (IllegalArgumentException | FileAlreadyExistsException e) {
            throw DatabaseExceptionBuilder.buildInvalidDbPathException(e);
        }
    }
}
