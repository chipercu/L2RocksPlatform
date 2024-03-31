package com.fuzzy.platform.component.database;

import com.fuzzy.cluster.exception.ClusterException;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBProvider;
import com.fuzzy.platform.component.database.DatabaseComponentExtension;
import com.fuzzy.platform.component.database.DatabaseConsts;
import com.fuzzy.platform.component.database.configure.DatabaseConfigure;
import com.fuzzy.platform.component.database.remote.cfconfig.ColumnFamilyConfigService;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.rocksdb.RocksDBProvider;
import com.fuzzy.rocksdb.RocksDataBaseBuilder;
import com.fuzzy.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.HashMap;

@com.fuzzy.cluster.anotation.Info(uuid = DatabaseConsts.UUID)
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

    @Override
    public QuerySystem<Void> onStart() {
        DatabaseComponentExtension extension = databaseConfigure.extension;
        if (extension != null) {
            extension.onStart(this);
        }
        return null;
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
