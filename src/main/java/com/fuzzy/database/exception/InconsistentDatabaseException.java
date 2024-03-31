package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class InconsistentDatabaseException extends DatabaseException {

    public InconsistentDatabaseException(String message) {
        super(message);
    }

    public InconsistentDatabaseException(Throwable cause) {
        super(cause);
    }

    public InconsistentDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
