package com.fuzzy.main.rdao.rocksdb.backup;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.utils.PathUtils;
import com.fuzzy.main.rdao.rocksdb.RocksDBProvider;
import com.fuzzy.main.rdao.rocksdb.RocksDataBaseBuilder;
import org.rocksdb.*;

import java.nio.file.Path;
import java.util.List;

public class RocksDBCreateBackup {

    private final RocksDB rocksDB;

    public RocksDBCreateBackup(RocksDB rocksDB) {
        this.rocksDB = rocksDB;
    }

    public void create(Path backupPath) throws DatabaseException {
        PathUtils.checkPath(backupPath);

        try (BackupableDBOptions dbOptions = new BackupableDBOptions(backupPath.toString());
             BackupEngine backupEngine = BackupEngine.open(Env.getDefault(), dbOptions)) {

            final boolean flushBeforeBackup = true;
            try {
                backupEngine.createNewBackup(rocksDB, flushBeforeBackup);
            } catch (RocksDBException e) {
                if (e.getStatus() == null || e.getStatus().getCode() != Status.Code.Corruption) {
                    throw e;
                }

                backupEngine.purgeOldBackups(0);
                backupEngine.createNewBackup(rocksDB, flushBeforeBackup);
            }

            List<BackupInfo> backups = backupEngine.getBackupInfo();
            if (backups.isEmpty()) {
                throw new RocksDBException("BackupEngine::getBackupInfo return empty list.");
            }
            for (int corruptedBackup : backupEngine.getCorruptedBackups()) {
                backupEngine.deleteBackup(corruptedBackup);
            }
            backupEngine.purgeOldBackups(1);

            rocksDB.compactRange();
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e.getMessage());
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    public static void createOfStoppedDatabase(Path backupPath, Path dbPath) {
        try (RocksDBProvider dataBase = new RocksDataBaseBuilder()
                .withPath(dbPath)
                .build()) {
            RocksDBCreateBackup rocksDBCreateBackup = new RocksDBCreateBackup(dataBase.getRocksDB());
            rocksDBCreateBackup.create(backupPath);
        }
    }
}
