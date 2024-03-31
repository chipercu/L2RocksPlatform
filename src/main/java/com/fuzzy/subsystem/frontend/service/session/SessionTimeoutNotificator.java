package com.fuzzy.subsystem.frontend.service.session;

import com.fuzzy.platform.sdk.context.impl.ContextImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.remote.logon.LogoutCause;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Ulitin V. Необходимо переписать, под большой нагрузкой в ГПБ много логов вида:
 * 2020-12-07 16:09:43.882 [00007631] WARN  c.i.s.q.service.DetectLongQuery:66 - Detect long query (trace: r1246634579), start: 2020-12-07T13:09:34.238Z, duration: 7921, resources: { shared: [ApiKeyCorePrivilegeReadable, ApiKeyDashboardPrivilegeReadable, ApiKeyClickHouseStandalonePrivilegeReadable, ApiKeyReadable, ApiKeyMonitoringPrivilegeReadable, ApiKeyClickHouseCorePrivilegeReadable]}, stackTrace: [sun.misc.Unsafe.park(Native Method) java.util.concurrent.locks.LockSupport.park(LockSupport.java:175) java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(AbstractQueuedSynchronizer.java:836) java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(AbstractQueuedSynchronizer.java:870) java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1199) java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(ReentrantLock.java:209) java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:285) java.util.concurrent.PriorityBlockingQueue.remove(PriorityBlockingQueue.java:663) com.infomaximum.subsystem.frontend.service.session.SessionTimeoutNotificator.removeSessionTimeout(SessionTimeoutNotificator.java:74) com.infomaximum.subsystem.frontend.service.session.SessionTimeoutNotificator.addSessionTimeout(SessionTimeoutNotificator.java:79) com.infomaximum.subsystem.activedirectory.auth.session.SessionServiceApiKeyADImpl.getAuthSession(SessionServiceApiKeyADImpl.java:117) com.infomaximum.subsystem.activedirectory.remote.authcontext.RControllerBuilderApiKeyADAuthImpl.auth(RControllerBuilderApiKeyADAuthImpl.java:61) com.infomaximum.subsystem.frontend.component.authcontext.AuthContextComponent.getAuthContext(AuthContextComponent.java:62) com.infomaximum.subsystem.frontend.service.authorize.RequestAuthorizeImpl.authorize(RequestAuthorizeImpl.java:40) com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService$1.execute(GraphQLRequestExecuteService.java:122) com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService$1.execute(GraphQLRequestExecuteService.java:97) com.infomaximum.subsystems.querypool.QueryPool$QueryWrapper.execute(QueryPool.java:91) com.infomaximum.subsystems.querypool.QueryPool$QueryWrapper.access$100(QueryPool.java:34) com.infomaximum.subsystems.querypool.QueryPool.lambda$submitQuery$5(QueryPool.java:334) com.infomaximum.subsystems.querypool.QueryPool$$Lambda$91/1503375838.run(Unknown Source) java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) java.util.concurrent.FutureTask.run(FutureTask.java:266) java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) java.lang.Thread.run(Thread.java:748)]
 */
public class SessionTimeoutNotificator extends Thread {

	private final static Logger log = LoggerFactory.getLogger(SessionTimeoutNotificator.class);

	private final AtomicLong rejectedCount = new AtomicLong(0);
	private final Lock lock = new ReentrantLock();
	private final SessionTimeoutQueue sessionTimeoutQueue = new SessionTimeoutQueue();
	private final Map<String, Session> sessionInfo = new ConcurrentHashMap<>();
	private final SessionService sessionService;
	private final String identificator;
	private final Thread logThread;

	public SessionTimeoutNotificator(SessionService sessionService, String identificator) {
		this.sessionService = sessionService;
		this.identificator = identificator;
		this.logThread = new Thread(this::writeToLog, "SessionTimeoutNotificatorLogThread");
	}

	@Override
	public void run() {
		this.logThread.start();
		while (true) {
			try {
				SessionTimeout st;
				lock.lock();
				try {
					st = sessionTimeoutQueue.peek();
				} finally {
					lock.unlock();
				}
				if (st != null) {
					Instant nextTime = st.getTimeout();
					Instant now = Instant.now();
					if (now.isBefore(nextTime)) {
						Thread.sleep(Duration.between(Instant.now(), nextTime).toMillis());
					}
					Session session = null;
					boolean logoutByTimeout;
					lock.lock();
					try {
						SessionTimeout stHead = sessionTimeoutQueue.peek();
						logoutByTimeout = st == stHead && !sessionService.getSessionsAsMap().containsKey(st.getUuid());
						if (logoutByTimeout) {
							sessionTimeoutQueue.remove(st.getUuid());
							session = sessionInfo.remove(st.getUuid());
						}
					} finally {
						lock.unlock();
					}
					if (logoutByTimeout) {
						if (session != null) {
							logLogoutByTimeout(session);
						} else {
							log.error("Session: {} not found!", st.getUuid());
						}
					}
				} else {
					Thread.sleep(sessionService.getSessionTimeout());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void putSessionInfo(String sessionUuid, Session session) {
		sessionInfo.put(sessionUuid, session);
	}

	public void removeSessionInfo(String sessionUuid) {
		sessionInfo.remove(sessionUuid);
	}

	public void removeSessionTimeout(SessionTimeout sessionTimeout) {
		timesQueueExec(sessionTimeoutQueue ->
				execWithLogging("sessionTimeoutQueue.remove", () -> sessionTimeoutQueue.remove(sessionTimeout.getUuid())));
	}

	public void addSessionTimeout(String sessionUuid) {
		timesQueueExec(sessionTimeoutQueue -> {
			SessionTimeout st = new SessionTimeout(sessionUuid, Instant.now().plus(sessionService.getSessionTimeout(), ChronoUnit.MILLIS));
			execWithLogging("sessionTimeoutQueue.add", () -> sessionTimeoutQueue.updateOrAdd(st));
		});
	}

	public void addSessionTimeout(SessionTimeout st) {
		timesQueueExec(sessionTimeoutQueue ->
				execWithLogging("sessionTimeoutQueue.add", () -> sessionTimeoutQueue.updateOrAdd(st)));
	}

	public void clear() {
		timesQueueExec(SessionTimeoutQueue::clear);
		sessionInfo.clear();
	}


	private static void logLogoutByTimeout(Session session) {
		if (session == null) {
			throw new IllegalArgumentException();
		}

		SecurityLog.info(
				new SyslogStructDataEvent(CoreEvent.Employee.TYPE_LOGOUT)
						.withParam(CoreParameter.Employee.CAUSE, LogoutCause.TIMEOUT.name().toLowerCase())
						.withParam(CoreParameter.Employee.SESSION_HASH, session.uuid),
				session.getSysLogDataTarget(),
				new ContextImpl(new SourceSystemImpl())
		);
	}

	private void timesQueueExec(Consumer<SessionTimeoutQueue> consumer) {
		try {
			if (!lock.tryLock(500, TimeUnit.MILLISECONDS)) {
				rejectedCount.incrementAndGet();
				return;
			}
			try {
				consumer.accept(sessionTimeoutQueue);
			} finally {
				lock.unlock();
			}
		} catch (InterruptedException ignored) { }
	}

	private void execWithLogging(String action, Runnable runnable) {
		long time = System.currentTimeMillis();
		runnable.run();
		time = System.currentTimeMillis() - time;
		if (time > 250) {
			log.info(String.format("ID = %s, sessionTimeoutQueue size = %s, rejectedCount = %s, action = %s, time = %s ms",
					identificator, sessionTimeoutQueue.size(), rejectedCount.get(), action, time));
		}
	}

	private void writeToLog() {
		for (;;) {
			try {
				Thread.sleep(Duration.ofMinutes(5).toMillis());
			} catch (InterruptedException e) {
				break;
			}
			int timesQueueSize;
			lock.lock();
			try {
				timesQueueSize = sessionTimeoutQueue.size();
			} finally {
				lock.unlock();
			}
			log.info(String.format("ID = %s, timesQueue size = %s, rejectedCount = %s",
					identificator, timesQueueSize, rejectedCount.get()));
		}
	}
}
