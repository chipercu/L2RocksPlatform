package com.fuzzy.main.rdao.database;

import com.fuzzy.main.rdao.database.domainobject.filter.*;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBProvider;
import com.fuzzy.main.rdao.database.provider.DBTransaction;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBSchema;

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
        void action(final DataCommand transaction) throws Exception;
    }

    @FunctionalInterface
    public interface Function<R> {

        /**
         * Реализация операции.
         * @param transaction Контекст, в котором выполняется операция.
         * @throws Exception Если во время выполнения операции возникла ошибка.
         */
        R apply(final DataCommand transaction) throws Exception;
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

    private DataCommand buildDataCommand(DBTransaction transaction) {
        return new DataCommand(transaction, dbSchema);
    }
}
