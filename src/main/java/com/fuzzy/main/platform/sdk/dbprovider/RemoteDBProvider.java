package com.fuzzy.main.platform.sdk.dbprovider;

import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.dbprovider.remote.RControllerDBProvider;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.*;
import com.fuzzy.main.rdao.rocksdb.options.columnfamily.ColumnFamilyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteDBProvider implements DBProvider {

    private final static Logger log = LoggerFactory.getLogger(RemoteDBProvider.class);

    private final Component component;

    RemoteDBProvider(Component component) {
        this.component = component;
    }

    @Override
    public DBIterator createIterator(String columnFamily) throws DatabaseException {
        try {
            return new Iterator(getRemoteProvider().createIterator(columnFamily));
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public DBTransaction beginTransaction() throws DatabaseException {
        try {
            return new Transaction(getRemoteProvider().beginTransaction());
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
        try {
            return getRemoteProvider().getValue(columnFamily, key);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean containsColumnFamily(String name) throws DatabaseException {
        try {
            return getRemoteProvider().containsColumnFamily(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public String[] getColumnFamilies() throws DatabaseException {
        try {
            return getRemoteProvider().getColumnFamilies();
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void createColumnFamily(String name) throws DatabaseException {
        try {
            getRemoteProvider().createColumnFamily(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void createColumnFamily(String name, ColumnFamilyConfig options) throws DatabaseException {
        try {
            getRemoteProvider().createColumnFamily(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }


    @Override
    public void dropColumnFamily(String name) throws DatabaseException {
        try {
            getRemoteProvider().dropColumnFamily(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void compactRange() throws DatabaseException {
        try {
            getRemoteProvider().compactRange();
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean containsSequence(String name) throws DatabaseException {
        try {
            return getRemoteProvider().containsSequence(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void createSequence(String name) throws DatabaseException {
        try {
            getRemoteProvider().createSequence(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void dropSequence(String name) throws DatabaseException {
        try {
            getRemoteProvider().dropSequence(name);
        } catch (PlatformException e) {
            throw new DatabaseException(e);
        }
    }

    private RControllerDBProvider getRemoteProvider() {
        throw new RuntimeException("Not implemented");
        //TODO not implemented
//        return component.getRemotes().getFromCKey(shardKey, RControllerDBProvider.class);
    }

    private class Iterator implements DBIterator {

        private final long iteratorId;

        Iterator(long iteratorId) {
            this.iteratorId = iteratorId;
        }

        @Override
        public KeyValue seek(KeyPattern pattern) throws DatabaseException {
            try {
                return getRemoteProvider().seekIterator(pattern, iteratorId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public KeyValue next() throws DatabaseException {
            try {
                return getRemoteProvider().nextIterator(iteratorId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public KeyValue step(StepDirection direction) throws DatabaseException {
            try {
                return getRemoteProvider().stepIterator(direction, iteratorId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void close() throws DatabaseException {
            try {
                getRemoteProvider().closeIterator(iteratorId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }
    }

    private class Transaction implements DBTransaction {

        private final long transactionId;

        Transaction(long transactionId) {
            this.transactionId = transactionId;
        }

        @Override
        public DBIterator createIterator(String columnFamily) throws DatabaseException {
            try {
                return new Iterator(getRemoteProvider().createIteratorTransaction(columnFamily, transactionId));
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public long nextId(String sequenceName) throws DatabaseException {
            try {
                return getRemoteProvider().nextIdTransaction(sequenceName, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
            try {
                return getRemoteProvider().getValueTransaction(columnFamily, key, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void put(String columnFamily, byte[] key, byte[] value) throws DatabaseException {
            try {
                getRemoteProvider().putTransaction(columnFamily, key, value, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void delete(String columnFamily, byte[] key) throws DatabaseException {
            try {
                getRemoteProvider().deleteTransaction(columnFamily, key, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void deleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException {
            try {
                getRemoteProvider().deleteRangeTransaction(columnFamily, beginKey, endKey, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void singleDelete(String columnFamily, byte[] key) throws DatabaseException {
            try {
                getRemoteProvider().singleDeleteTransaction(columnFamily, key, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void singleDeleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException {
            try {
                getRemoteProvider().singleDeleteRangeTransaction(columnFamily, beginKey, endKey, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void singleDeleteRange(String columnFamily, KeyPattern keyPattern) throws DatabaseException {
            try {
                getRemoteProvider().singleDeleteRangeTransaction(columnFamily, keyPattern, transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void commit() throws DatabaseException {
            try {
                getRemoteProvider().commitTransaction(transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void rollback() throws DatabaseException {
            try {
                getRemoteProvider().rollbackTransaction(transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void compactRange() throws DatabaseException {
            try {
                getRemoteProvider().compactRange();
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }

        @Override
        public void close() throws DatabaseException {
            try {
                getRemoteProvider().closeTransaction(transactionId);
            } catch (PlatformException e) {
                throw new DatabaseException(e);
            }
        }
    }

    @Override
    public void close() {
        log.error("Not implemented!");
        //TODO not implemented
    }
}
