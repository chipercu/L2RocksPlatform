package com.fuzzy.main.platform.component.database.exception;

import com.fuzzy.main.platform.component.database.DatabaseConsts;
import com.fuzzy.main.platform.exception.ExceptionFactory;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.exception.PlatformExceptionFactory;

public class DatabaseExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new PlatformExceptionFactory(DatabaseConsts.UUID);

    public static PlatformException buildBackupException(Throwable e) {
        return EXCEPTION_FACTORY.build("backup_error", e);
    }

    public static PlatformException buildRestoreException(Throwable e) {
        return EXCEPTION_FACTORY.build("restore_error", e);
    }

    public static PlatformException buildInvalidDbPathException() {
        return buildInvalidDbPathException(null);
    }

    public static PlatformException buildInvalidDbPathException(Throwable e) {
        return EXCEPTION_FACTORY.build("invalid_db_path", e);
    }

    public static PlatformException buildInvalidBackupNameException() {
        return EXCEPTION_FACTORY.build("invalid_backup_name");
    }

    public static PlatformException buildInvalidBackupPathException() {
        return buildInvalidBackupPathException(null);
    }

    public static PlatformException buildInvalidBackupPathException(Throwable e) {
        return EXCEPTION_FACTORY.build("invalid_backup_path", e);
    }
}
