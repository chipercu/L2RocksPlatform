package com.fuzzy.platform.querypool.service;

import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.service.utils.QueryPoolUtils;
import com.fuzzy.platform.sdk.context.Context;
import com.fuzzy.platform.sdk.context.ContextUtils;
import com.fuzzy.platform.utils.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис отслеживающий долгих query
 */
public class DetectLongQuery implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(DetectLongQuery.class);

	private final static Duration DETECTED_PERIOD = Duration.ofSeconds(1);
	private final static Duration CHECK_PERIOD = Duration.ofMillis(DETECTED_PERIOD.toMillis() - 100);

	private final static Duration WARN_LOG_DETECTED_PERIOD =  Duration.ofSeconds(30);

	private final QueryPool queryPool;
	private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private final ScheduledExecutorService scheduler;

	public DetectLongQuery(QueryPool queryPool, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.queryPool = queryPool;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;

		this.scheduler = Executors.newScheduledThreadPool(
				1,
				new DefaultThreadFactory("QueryPool-DetectLongQuery", uncaughtExceptionHandler)
		);
		scheduler.scheduleAtFixedRate(this, CHECK_PERIOD.toMillis(), CHECK_PERIOD.toMillis(), TimeUnit.MILLISECONDS);

		log.info("DetectLongQuery starting.");
	}

	@Override
	public void run() {
		try {
			Instant now = Instant.now();
			for (QueryPool.QueryWrapper queryWrapper : queryPool.getExecuteQueries()) {
				if (queryWrapper.getTimeComplete() != null) continue;

				Instant timeStart = queryWrapper.getTimeStart();
				if (timeStart == null) continue;

				Duration duration = Duration.between(timeStart, now);
				if (duration.compareTo(DETECTED_PERIOD) < 0) continue;

				Map<String, QueryPool.LockType> resources = queryWrapper.getResources();
				if (resources.isEmpty()) continue;

				Thread thread = queryWrapper.getThread();
				if (thread == null) continue;

				String sTimeStart = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.ofInstant(timeStart, ZoneId.systemDefault()));
				Context context = queryWrapper.getContext();

				//Подумать над оптимизацияе -  не стоит формировать строки, если система нагружена и все равно не выводит лог
				if (duration.compareTo(WARN_LOG_DETECTED_PERIOD) < 0) {
					log.debug("Detect long query {}, start: {}, duration: {}, resources: {}, stackTrace: {}",
							ContextUtils.toTrace(context),
							sTimeStart,
							duration.toMillis(),
							QueryPoolUtils.toStringResources(resources),
							QueryPoolUtils.toStringStackTrace(thread)
					);
				} else {
					log.warn("Detect long query {}, start: {}, duration: {}, resources: {}, stackTrace: {}",
							ContextUtils.toTrace(context),
							sTimeStart,
							duration.toMillis(),
							QueryPoolUtils.toStringResources(resources),
							QueryPoolUtils.toStringStackTrace(thread)
					);
				}
			}
		} catch (Throwable e) {
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
		}
	}


	public void shutdownAwait() {
		scheduler.shutdown();
		log.info("DetectLongQuery stopping.");
	}

}
