package com.fuzzy.main.network.protocol;

import com.fuzzy.main.network.session.TransportSession;
import com.fuzzy.main.network.transport.Transport;

import java.lang.reflect.InvocationTargetException;

public abstract class Protocol {

    public final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public Protocol(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public abstract String getName();

    public abstract TransportSession onConnect(Transport transport, Object channel) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, Exception;
}
