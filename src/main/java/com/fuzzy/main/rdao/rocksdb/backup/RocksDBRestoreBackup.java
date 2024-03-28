package com.fuzzy.main.rdao.rocksdb.backup;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.utils.PathUtils;
import org.rocksdb.*;

import java.nio.file.Path;

public class RocksDBRestoreBackup {

    public static void restore(Path backupPath, Path dbPath) throws DatabaseException {
        PathUtils.checkPath(backupPath);
        PathUtils.checkPath(dbPath);

        RocksDB.loadLibrary();

        try (BackupableDBOptions dbOptions = new BackupableDBOptions(backupPath.toString());
             BackupEngine backupEngine = BackupEngine.open(Env.getDefault(), dbOptions);
             RestoreOptions restoreOptions = new RestoreOptions(false)) {

            final String dbDir = dbPath.toString();
            backupEngine.restoreDbFromLatestBackup(dbDir, dbDir, restoreOptions);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e.getMessage());
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }
}
