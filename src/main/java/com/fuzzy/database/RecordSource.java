package com.fuzzy.database;

import com.fuzzy.database.DataCommand;
import com.fuzzy.database.DataReadCommand;
import com.fuzzy.database.Record;
import com.fuzzy.database.RecordIterator;
import com.fuzzy.database.domainobject.filter.*;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBProvider;
import com.fuzzy.database.provider.DBTransaction;
import com.fuzzy.database.schema.Schema;
import com.fuzzy.database.schema.dbstruct.DBSchema;

public class RecordSource {

    private final DBProvider dbProvider;
    private final DBSchema dbSchema;

    @FunctionalInterface
    public interface Monad {

        /**
         * Реализация операции.
         * @param transaction Контекст, в котором выполняется операция.
         * @throws Exception Если во время выполнения операции возникла ошибка.
         */
        void action(final com.fuzzy.database.DataCommand transaction) throws Exception;
    }

    @FunctionalInterface
    public interface Function<R> {

        /**
         * Реализация операции.
         * @param transaction Контекст, в котором выполняется операция.
         * @throws Exception Если во время выполнения операции возникла ошибка.
         */
        R apply(final com.fuzzy.database.DataCommand transaction) throws Exception;
    }

    public RecordSource(DBProvider dbProvider) throws DatabaseException {
        this.dbProvider = dbProvider;
        this.dbSchema = Schema.read(dbProvider).getDbSchema();
    }

    public Record getById(String table, String namespace, long id) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).getById(table, namespace, id);
    }

    public RecordIterator select(String table, String namespace) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).select(table, namespace);
    }

    public RecordIterator select(String table, String namespace, HashFilter filter) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).select(table, namespace, filter);
    }

    public RecordIterator select(String table, String namespace, PrefixFilter filter) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).select(table, namespace, filter);
    }

    public RecordIterator select(String table, String namespace, IntervalFilter filter) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).select(table, namespace, filter);
    }

    public RecordIterator select(String table, String namespace, RangeFilter filter) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).select(table, namespace, filter);
    }

    public RecordIterator select(String table, String namespace, IdFilter filter) throws DatabaseException {
        return new DataReadCommand(dbProvider, dbSchema).select(table, namespace, filter);
    }

    public void executeTransactional(final Monad operation) throws Exception {
        try (DBTransaction transaction = dbProvider.beginTransaction()) {
            operation.action(buildDataCommand(transaction));
            transaction.commit();
        }
    }

    public <R> R executeFunctionTransactional(final Function<R> function) throws Exception {
        try (DBTransaction transaction = dbProvider.beginTransaction()) {
            R result = function.apply(buildDataCommand(transaction));
            transaction.commit();
            return result;
        }
    }

    private com.fuzzy.database.DataCommand buildDataCommand(DBTransaction transaction) {
        return new DataCommand(transaction, dbSchema);
    }
}
