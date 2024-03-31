package com.fuzzy.platform.sdk.dbprovider;

import com.fuzzy.cluster.Cluster;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.DBProvider;
import com.fuzzy.database.provider.DBTransaction;
import com.fuzzy.platform.component.database.DatabaseComponent;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.platform.sdk.dbprovider.RemoteDBProvider;
import com.fuzzy.rocksdb.options.columnfamily.ColumnFamilyConfig;

public class ComponentDBProvider implements DBProvider {

    private final DBProvider dbProvider;

    public ComponentDBProvider(Cluster cluster, Component component) {
        Component databaseComponent = cluster.getAnyLocalComponent(DatabaseComponent.class);
        if (databaseComponent != null) {
            //Отлично, база у нас локальная - Работаем напрямую
            dbProvider = databaseComponent.getDbProvider();
        } else {
            //База удаленая уходим на remote
            dbProvider = new com.fuzzy.platform.sdk.dbprovider.RemoteDBProvider(component);
        }
    }

    @Override
    public DBIterator createIterator(String columnFamily) throws DatabaseException {
        return dbProvider.createIterator(columnFamily);
    }

    @Override
    public DBTransaction beginTransaction() throws DatabaseException {
        return dbProvider.beginTransaction();
    }

    @Override
    public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
        return dbProvider.getValue(columnFamily, key);
    }

    @Override
    public boolean containsColumnFamily(String name) throws DatabaseException {
        return dbProvider.containsColumnFamily(name);
    }

    @Override
    public String[] getColumnFamilies() throws DatabaseException {
        return dbProvider.getColumnFamilies();
    }

    @Override
    public void createColumnFamily(String name) throws DatabaseException {
        dbProvider.createColumnFamily(name);
    }

    @Override
    public void createColumnFamily(String name, ColumnFamilyConfig options) throws DatabaseException {
        dbProvider.createColumnFamily(name, options);
    }

    @Override
    public void dropColumnFamily(String name) throws DatabaseException {
        dbProvider.dropColumnFamily(name);
    }

    @Override
    public void compactRange() throws DatabaseException {
        dbProvider.compactRange();
    }

    @Override
    public boolean containsSequence(String name) throws DatabaseException {
        return dbProvider.containsSequence(name);
    }

    @Override
    public void createSequence(String name) throws DatabaseException {
        dbProvider.createSequence(name);
    }

    @Override
    public void dropSequence(String name) throws DatabaseException {
        dbProvider.dropSequence(name);
    }

    @Override
    public void close() throws Exception {
        dbProvider.close();
    }
}