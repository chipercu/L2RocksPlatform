package com.fuzzy.main.platform.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectSource;
import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.exception.DatabaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс не потокобезопасен
 */
public class QueryTransaction implements AutoCloseable {

    @FunctionalInterface
    public interface CommitListener {
        void onCommitted();
    }

    @FunctionalInterface
    public interface RollbackListener {
        void onRollbacked(PlatformException cause);
    }

    private final Transaction transaction;

    private Map<ByteKey, CommitListener> commitListenerMap;
    private List<CommitListener> commitListeners;
    private List<RollbackListener> rollbackListeners;

    QueryTransaction(DomainObjectSource domainObjectSource) {
        transaction = domainObjectSource.buildTransaction();
    }

    public Transaction getDBTransaction() {
        return transaction;
    }

    public void addCommitListener(CommitListener listener) {
        if (closed()) {
            throw new RuntimeException("Нельзя добавлять слушателя после закрытия транзакции");
        }
        if (commitListeners == null){
            commitListeners = new ArrayList<>();
        }
        commitListeners.add(listener);
    }

    public void addCommitListener(byte[] key, CommitListener listener) {
        if (closed()) {
            throw new RuntimeException("Нельзя добавлять слушателя после закрытия транзакции");
        }
        if (commitListenerMap == null){
            commitListenerMap = new HashMap<>();
        }
        commitListenerMap.put(new ByteKey(key), listener);
    }

    public void addCommitListener(String key, CommitListener listener) {
        addCommitListener(key.getBytes(), listener);
    }

    public void addRollbackListener(RollbackListener listener) {
		if (closed()) {
			throw new RuntimeException("Нельзя добавлять слушателя после закрытия транзакции");
		}
        if (rollbackListeners == null) {
            rollbackListeners = new ArrayList<>();
        }
        rollbackListeners.add(listener);
    }

    /**
     * Discard all changes. Does not close the current internal transaction.
     */
    public void rollback() throws PlatformException {
        try {
            transaction.getDBTransaction().rollback();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    public boolean closed() {
        return transaction.isClosed();
    }

    /**
     * If the method failed, the transaction automatically rolls back
     */
    void commit() throws PlatformException {
        try {
            transaction.commit();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    void fireCommitListeners() {
        if (commitListeners != null) {
            //Переписывать сразу на итератор не стоит, так как пробежка по индексу решает проблему когда один из подписчиков
            //в момент комита выполняет код который в конечном итоге также подписывается на коммит(своеобразная рекурсия)
            //По этой же причине не реализован метод removeCommitListener
            for (CommitListener listener : commitListeners) {
                listener.onCommitted();
            }
        }
        if (commitListenerMap != null) {
            for (CommitListener listener : commitListenerMap.values()) {
                listener.onCommitted();
            }
        }
    }

    void fireRollbackListeners(PlatformException cause) {
        if (rollbackListeners != null) {
            for (RollbackListener listener : rollbackListeners) {
                listener.onRollbacked(cause);
            }
        }
    }

    @Override
    public void close() {
        try {
            transaction.close();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
