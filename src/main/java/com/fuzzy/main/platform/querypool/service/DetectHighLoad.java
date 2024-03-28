package com.fuzzy.main.platform.querypool.service;

import com.fuzzy.main.platform.querypool.QueryPool;
import com.fuzzy.main.platform.utils.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Сервис отслеживающий заканчивающиеся свободные worker'ы
 */
public class DetectHighLoad implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(DetectHighLoad.class);

    private final static Duration DETECTED_PERIOD = Duration.ofSeconds(10);
    private final static Duration CHECK_PERIOD = Duration.ofMillis(DETECTED_PERIOD.toMillis() - 100);

    private final QueryPool queryPool;
    private final ThreadPoolExecutor threadPool;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private final ScheduledExecutorService scheduler;

    public DetectHighLoad(QueryPool queryPool, ThreadPoolExecutor threadPool, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.queryPool = queryPool;
        this.threadPool = threadPool;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;

        this.scheduler = Executors.newScheduledThreadPool(
                1,
                new DefaultThreadFactory("QueryPool-DetectHighLoad", uncaughtExceptionHandler)
        );
        scheduler.scheduleAtFixedRate(this, CHECK_PERIOD.toMillis(), CHECK_PERIOD.toMillis(), TimeUnit.MILLISECONDS);

        log.info("DetectHighLoad starting.");
    }

    @Override
    public void run() {
        try {
            StringBuilder sMessage = null;

            //Детектим заканчивающиеся свободные веркеры
            int activeCount = threadPool.getActiveCount();
            int maximumPoolSize = threadPool.getMaximumPoolSize();
            if (maximumPoolSize - activeCount <= 2) {
                sMessage = new StringBuilder("On the verge of overload thread pool: ")
                        .append(activeCount).append('/').append(maximumPoolSize).append('.');
            }


			if (((QueryPool.MAX_WAITING_HIGH_QUERY_COUNT << 1) < queryPool.getHighPriorityWaitingQueryCount()) || //Детектим заполненость очереди высокоприоритетных запросов
                    ((QueryPool.MAX_WAITING_LOW_QUERY_COUNT << 1) < queryPool.getLowPriorityWaitingQueryCount())//Детектим заполненость очереди низкоприоритетными запросов

            ) {
				if (sMessage == null) {
					sMessage = new StringBuilder();
				} else {
					sMessage.append(' ');
				}
                sMessage.append("Query is full (High: ").append(queryPool.getHighPriorityWaitingQueryCount())
                        .append('/').append(QueryPool.MAX_WAITING_HIGH_QUERY_COUNT)
                        .append(") (Low").append(queryPool.getLowPriorityWaitingQueryCount())
                        .append('/').append(QueryPool.MAX_WAITING_LOW_QUERY_COUNT).append(").");
			}

			//Пишем в лог
            if (sMessage != null) {
                log.warn(sMessage.toString());
            }
        } catch (Throwable e) {
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
        }
    }

    public void shutdownAwait() {
        scheduler.shutdown();
        log.info("DetectHighLoad stopping.");
    }

}
