package com.fuzzy.network.exception;

import com.fuzzy.network.exception.NetworkException;

public class ParsePacketNetworkException extends NetworkException {

    public ParsePacketNetworkException(String message) {
        super(message);
    }

    public ParsePacketNetworkException(Throwable cause) {
        super(cause);
    }

    public ParsePacketNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
