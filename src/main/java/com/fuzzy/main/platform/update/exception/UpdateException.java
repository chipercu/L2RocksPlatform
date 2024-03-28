package com.fuzzy.main.platform.update.exception;

public class UpdateException extends RuntimeException {

    public UpdateException(String subsystemUuid, String message) {
        super("Update module " + subsystemUuid + " error. " + message);
    }

    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(String subsystemUuid, Throwable cause) {
        super("Update module " + subsystemUuid + " error.", cause);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }
}
