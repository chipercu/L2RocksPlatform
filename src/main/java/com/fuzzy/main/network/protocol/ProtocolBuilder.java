package com.fuzzy.main.network.protocol;

import com.fuzzy.main.network.exception.NetworkException;

public abstract class ProtocolBuilder {

    public abstract Protocol build(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException;
}
