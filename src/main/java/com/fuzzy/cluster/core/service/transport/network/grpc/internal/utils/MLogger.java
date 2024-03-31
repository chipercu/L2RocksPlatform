package com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils;


import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class MLogger {

    private final Logger log;
    private final int size;

    private final AtomicInteger counter;

    public MLogger(Logger log, int size) {
        this.log = log;
        this.size = size;
        this.counter = new AtomicInteger();
        reset();
    }

    public void reset() {
        counter.set(-1);
    }


    public void warn(String format, Object arg1, Object arg2){
        if (write()) {
            log.warn(format, arg1, arg2);
        }
    }

    private boolean write() {
        int count = counter.incrementAndGet();
        if (count == 0 || count > size) {//count==0 в случае первого запуска
            counter.getAndSet(0);
            return true;
        }
        return false;
    }
}
