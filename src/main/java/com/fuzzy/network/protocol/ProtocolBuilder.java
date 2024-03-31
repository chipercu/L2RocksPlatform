package com.fuzzy.network.protocol;

import com.fuzzy.network.exception.NetworkException;
import com.fuzzy.network.protocol.Protocol;

public abstract class ProtocolBuilder {

    public abstract Protocol build(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException;
}
