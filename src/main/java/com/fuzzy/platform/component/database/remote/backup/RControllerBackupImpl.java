package com.fuzzy.platform.component.database.remote.backup;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.component.database.DatabaseComponent;
import com.fuzzy.platform.component.database.exception.DatabaseExceptionBuilder;
import com.fuzzy.platform.component.database.remote.backup.RControllerBackup;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.exception.NotAbsolutePathException;
import com.fuzzy.platform.sdk.utils.FileUtils;
import com.fuzzy.platform.utils.StringUtils;
import com.fuzzy.rocksdb.backup.RocksDBCreateBackup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RControllerBackupImpl extends AbstractQueryRController<DatabaseComponent> implements RControllerBackup {

    public RControllerBackupImpl(DatabaseComponent component, ResourceProvider resources) {
        super(component, resources);
        resources.borrowAllDomainObjects(QueryPool.LockType.SHARED);
    }

    @Override
    public void createBackup(String backupDirPath, String backupName) throws PlatformException {
        Path backupPath = buildBackupPath(backupDirPath, backupName);
        FileUtils.ensureDirectory(backupPath);
        try {
            RocksDBCreateBackup rocksDBBackup =  component.getRocksDBProvider().getRocksDBBackup();
            rocksDBBackup.create(backupPath);
        } catch (DatabaseException e) {
            throw DatabaseExceptionBuilder.buildBackupException(e);
        }
    }

    public static Path buildBackupPath(String backupDirPath, String backupName) throws PlatformException {
        if (StringUtils.isEmpty(backupName) || !backupName.equals(new File(backupName).getName())) {
            throw DatabaseExceptionBuilder.buildInvalidBackupNameException();
        }
        if (StringUtils.isEmpty(backupDirPath)) {
            throw DatabaseExceptionBuilder.buildInvalidBackupPathException();
        }

        try {
            Path backupPath = Paths.get(backupDirPath);
            if (!backupPath.isAbsolute()) {
                throw new NotAbsolutePathException(backupDirPath);
            }
            return backupPath.resolve(backupName);
        } catch (IllegalArgumentException e) {
            throw DatabaseExceptionBuilder.buildInvalidBackupPathException(e);
        }
    }
}
