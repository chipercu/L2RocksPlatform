package com.fuzzy.database;

import com.fuzzy.database.DataCommand;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBTransaction;
import com.fuzzy.database.schema.dbstruct.DBSchema;

public class Transaction implements AutoCloseable {

    private final DBTransaction dbTransaction;
    private final com.fuzzy.database.DataCommand dataCommand;

    Transaction(DBTransaction dbTransaction, DBSchema schema) {
        this.dbTransaction = dbTransaction;
        this.dataCommand = new com.fuzzy.database.DataCommand(dbTransaction, schema);
    }

    public DataCommand getCommand() {
        return dataCommand;
    }

    public void commit() throws DatabaseException {
        dbTransaction.commit();
    }

    public void rollback() throws DatabaseException {
        dbTransaction.rollback();
    }

    @Override
    public void close() throws DatabaseException {
        dbTransaction.close();
    }
}
