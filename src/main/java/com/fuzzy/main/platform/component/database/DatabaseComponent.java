package com.fuzzy.main.platform.component.database;

import com.fuzzy.main.cluster.exception.ClusterException;
import com.fuzzy.main.platform.component.database.configure.DatabaseConfigure;
import com.fuzzy.main.platform.component.database.remote.cfconfig.ColumnFamilyConfigService;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBProvider;
import com.fuzzy.main.rdao.rocksdb.RocksDBProvider;
import com.fuzzy.main.rdao.rocksdb.RocksDataBaseBuilder;
import com.fuzzy.main.rdao.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.HashMap;

@com.fuzzy.main.cluster.anotation.Info(uuid = DatabaseConsts.UUID)
public class DatabaseComponent extends Component {

    private final DatabaseConfigure databaseConfigure;
    private volatile RocksDBProvider dbProvider;

    public DatabaseComponent(DatabaseConfigure databaseConfigure) {
        this.databaseConfigure = databaseConfigure;
    }

    @Override
    protected DBProvider initDBProvider() throws PlatformException {
        if (dbProvider != null) {
            return dbProvider;
        }
        try {
            final HashMap<String, ColumnFamilyConfig> configuredColumnFamilies = new ColumnFamilyConfigService(this).getConfigs();
            dbProvider = new RocksDataBaseBuilder()
                    .withPath(databaseConfigure.dbPath)
                    .withConfigColumnFamilies(configuredColumnFamilies)
                    .build();
            return dbProvider;
        } catch (DatabaseException e) {
            throw new ClusterException(e);
        }
    }

    public void onStarting() throws PlatformException {
        super.onStarting();

        DatabaseComponentExtension extension = databaseConfigure.extension;
        if (extension != null) {
            extension.initialize(this);
        }
    }

    public RocksDBProvider getRocksDBProvider() {
        return dbProvider;
    }

    public DatabaseConfigure getDatabaseConfigure() {
        return databaseConfigure;
    }

    @Override
    public final void destroy() {
        if (dbProvider != null) {
            dbProvider.close();
        }
        super.destroy();
    }
}
