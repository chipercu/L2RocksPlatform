package com.fuzzy.main.platform.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.service.DetectHighLoad;
import com.fuzzy.main.platform.querypool.service.DetectLongQuery;
import com.fuzzy.main.platform.querypool.service.DetectQueueFilling;
import com.fuzzy.main.platform.querypool.service.threadcontext.ThreadContext;
import com.fuzzy.main.platform.querypool.service.threadcontext.ThreadContextImpl;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.context.ContextTransactionInternal;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.platform.utils.DefaultThreadGroup;
import com.fuzzy.main.platform.utils.DefaultThreadPoolExecutor;
import com.fuzzy.main.platform.utils.LockGuard;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class QueryPool {

    public enum LockType {
        SHARED, EXCLUSIVE
    }

    public enum Priority {
        LOW, HIGH
    }

    @FunctionalInterface
    public interface Callback {

        void execute(QueryPool pool);
    }

    public static class QueryWrapper<T> {

        final QueryPool queryPool;
        final Component component;
        final ContextTransaction context;
        final Query<T> query;
        final QueryFuture<T> future;

        final Map<String, LockType> resources;

        private volatile Thread thread;
        private volatile Instant timeStart;
        private volatile Instant timeComplete;

        QueryWrapper(QueryPool queryPool, Component component, ContextTransaction context, Query<T> query) throws PlatformException {
            this(new QueryFuture<T>(queryPool, component, context, new CompletableFuture<>()), query);
        }

        QueryWrapper(QueryFuture<T> queryFuture, Query<T> query) throws PlatformException {
            this.queryPool = queryFuture.queryPool;
            this.component = queryFuture.component;
            this.context = queryFuture.context;
            this.query = query;
            this.future = queryFuture;

            try (ResourceProviderImpl provider = new ResourceProviderImpl(component)) {
                query.prepare(provider);
                this.resources = provider.getResources();
            }
        }

        public ContextTransaction getContext() {
            return context;
        }

        public Thread getThread() {
            return thread;
        }

        public Instant getTimeStart() {
            return timeStart;
        }

        public Instant getTimeComplete() {
            return timeComplete;
        }

        public Map<String, LockType> getResources() {
            return Collections.unmodifiableMap(resources);
        }

        private void execute() {
            timeStart = Instant.now();
            thread = Thread.currentThread();
            try (QueryTransaction transaction = new QueryTransaction(component.getDomainObjectSource())) {
                try {
                    ((ContextTransactionInternal) context).setTransaction(transaction);
                    queryPool.threadContext.setContext(context);

                    T result = query.execute(transaction);
                    transaction.commit();
                    transaction.fireCommitListeners();
                    future.complete(result);
                } catch (PlatformException e) {
                    transaction.fireRollbackListeners(e);
                    future.completeExceptionally(e);
                }
            } catch (Throwable e) {
                future.completeExceptionally(e);
                throw e;
            } finally {
                timeComplete = Instant.now();
                queryPool.detectQueueFilling.queryComplete(this, timeStart, timeComplete);
                thread = null;
                queryPool.threadContext.clearContext();
            }
        }
    }

    private static class QueryLockType {

        final QueryWrapper query;
        final LockType lockType;

        QueryLockType(QueryWrapper query, LockType lockType) {
            this.query = query;
            this.lockType = lockType;
        }
    }

    private static class ResourceMap extends HashMap<String, ArrayList<QueryLockType>> {
    }

    //TODO Удалить костыль
    public static final int MAX_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 80;
    public static final int MAX_WORKED_QUERY_COUNT = MAX_THREAD_COUNT * 5;

    /**
     * Пользовательские запросы более приоритетны, потоэму лучше их положить в очередь, чем сразу кинуть ошибку
     */
    public static final int MAX_WAITING_HIGH_QUERY_COUNT = MAX_THREAD_COUNT * 20;
    /**
     * Очередь для низко приоритетных запросов в четыре раза меньше пула потоков данных, сделана для того,
     * что бы сервер не захлебывался в моменты пиковой нагрузки
     */
    public static final int MAX_WAITING_LOW_QUERY_COUNT = MAX_THREAD_COUNT / 4;

    private final ThreadPoolExecutor threadPool;

    private final ReentrantLock lock = new ReentrantLock();
    private final ArrayList<String> maintenanceMarkers = new ArrayList<>();
    private final ResourceMap occupiedResources = new ResourceMap();
    private final ResourceMap waitingResources = new ResourceMap();
    private final ArrayList<Callback> emptyPoolListners = new ArrayList<>();

    private final DetectLongQuery detectLongQuery;
    private final DetectHighLoad detectHighLoad;
    private final DetectQueueFilling detectQueueFilling;
    private final ThreadContextImpl threadContext;

    private volatile int highPriorityWaitingQueryCount = 0;
    private volatile int lowPriorityWaitingQueryCount = 0;
    private volatile PlatformException hardException = null;

    public QueryPool(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        DefaultThreadGroup defaultThreadGroup = new DefaultThreadGroup("QueryPool", uncaughtExceptionHandler);

        this.threadPool = new DefaultThreadPoolExecutor(
                MAX_THREAD_COUNT,
                MAX_THREAD_COUNT,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(MAX_WORKED_QUERY_COUNT),
                defaultThreadGroup
        );
        this.detectLongQuery = new DetectLongQuery(this, uncaughtExceptionHandler);
        this.detectHighLoad = new DetectHighLoad(this, threadPool, uncaughtExceptionHandler);
        this.detectQueueFilling = new DetectQueueFilling();
        this.threadContext = new ThreadContextImpl(defaultThreadGroup);
    }

    public void setHardException(PlatformException e) {
        hardException = e;
    }

    protected <T> void execute(QueryFuture<T> queryFuture, Query<T> query, boolean failIfPoolBusy) {
        QueryWrapper<T> queryWrapp;
        try {
            queryWrapp = new QueryWrapper<>(queryFuture, query);
        } catch (PlatformException e) {
            queryFuture.completeExceptionally(e);
            return;
        }
        execute(queryWrapp, failIfPoolBusy);
    }

    public <T> QueryFuture<T> execute(Component component, Query<T> query) {
        return execute(component, null, query, true);
    }

    public <T> QueryFuture<T> execute(Component component, ContextTransaction context, Query<T> query) {
        return execute(component, context, query, true);
    }

    public <T> QueryFuture<T> execute(Component component, ContextTransaction context, Query<T> query, boolean failIfPoolBusy) {
        QueryWrapper<T> queryWrapp;
        try {
            queryWrapp = new QueryWrapper<>(this, component, context, query);
        } catch (PlatformException e) {
            CompletableFuture future = new CompletableFuture();
            future.completeExceptionally(e);
            return new QueryFuture<>(this, component, context, future);
        }
        return execute(queryWrapp, failIfPoolBusy);
    }

    protected <T> QueryFuture<T> execute(QueryWrapper<T> queryWrapp, boolean failIfPoolBusy) {
        if (hardException != null) {
            queryWrapp.future.completeExceptionally(hardException);
            return queryWrapp.future;
        }

        try (LockGuard guard = new LockGuard(lock)) {
            if (failIfPoolBusy && isOverloaded(queryWrapp.query.getPriority())) {
                queryWrapp.future.completeExceptionally(createOverloadedException());
            } else if (isOccupiedResources(queryWrapp.resources)) {
                if (failIfPoolBusy && isMaintenance()) {
                    queryWrapp.future.completeExceptionally(createMaintenanceException());
                } else {
                    captureWaitingResources(queryWrapp);
                }
            } else {
                submitQuery(queryWrapp);
            }
        }

        return queryWrapp.future;
    }

    public boolean isBusyFor(Priority priority) {
        try (LockGuard guard = new LockGuard(lock)) {
            return isMaintenance() || isOverloaded(priority);
        }
    }

    /**
     * @return null if query is not submitted
     */
    public <T> QueryFuture<T> tryExecuteImmediately(Component component, Query<T> query) throws PlatformException {
        return tryExecuteImmediately(component, null, query);
    }

    /**
     * @return null if query is not submitted
     */
    public <T> QueryFuture<T> tryExecuteImmediately(Component component, ContextTransaction context, Query<T> query) throws PlatformException {
        if (hardException != null) {
            return null;
        }

        QueryWrapper<T> queryWrapp = new QueryWrapper<>(this, component, context, query);

        try (LockGuard guard = new LockGuard(lock)) {
            if (isOverloaded(queryWrapp.query.getPriority()) || isOccupiedResources(queryWrapp.resources)) {
                return null;
            }
            submitQuery(queryWrapp);
            return queryWrapp.future;
        }
    }

    public void addEmptyReachedListner(Callback callback) {
        try (LockGuard guard = new LockGuard(lock)) {
            emptyPoolListners.add(callback);
        }
    }

    public void removeEmptyReachedListner(Callback callback) {
        try (LockGuard guard = new LockGuard(lock)) {
            emptyPoolListners.remove(callback);
        }
    }

    public void tryFireEmptyReachedListener() {
        Callback[] listeners;
        try (LockGuard guard = new LockGuard(lock)) {
            listeners = getFiringEmptyPoolListners();
        }
        fireEmptyPoolListners(listeners);
    }

    public boolean waitingQueryExists(Priority priority) {
        switch (priority) {
            case LOW:
                return lowPriorityWaitingQueryCount != 0;
            case HIGH:
                return highPriorityWaitingQueryCount != 0;
        }
        return false;
    }

    public Collection<QueryWrapper> getExecuteQueries() {
        final HashSet<QueryWrapper> queries = new HashSet<>();
        try (LockGuard guard = new LockGuard(lock)) {
            occupiedResources.forEach((key, value) -> value.forEach(item -> queries.add(item.query)));
        }
        return queries;
    }

    public ThreadContext getThreadContext(){
        return threadContext;
    }

    public void shutdownAwait() throws InterruptedException {
        detectLongQuery.shutdownAwait();
        detectHighLoad.shutdownAwait();

        threadPool.shutdown();

        final HashSet<QueryWrapper> queries = new HashSet<>();
        try (LockGuard guard = new LockGuard(lock)) {
            waitingResources.forEach((key, value) -> value.forEach(item -> queries.add(item.query)));
            waitingResources.clear();
        }
        queries.forEach((query) -> query.future.completeExceptionally(GeneralExceptionBuilder.buildServerShutsDownException()));

        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public boolean isShutdown() {
        return threadPool.isShutdown();
    }

    public void await() throws InterruptedException {
        while (true) {
            try (LockGuard guard = new LockGuard(lock)) {
                if (waitingResources.isEmpty()) {
                    break;
                }
            }

            Thread.sleep(500L);
        }

        shutdownAwait();
    }

    private void submitQuery(QueryWrapper<?> queryWrapp) {
        captureOccupiedResources(queryWrapp);

        try {
            threadPool.submit(() -> {
                try {
                    queryWrapp.execute();
                } catch (Throwable e) {
                    try (LockGuard guard = new LockGuard(lock)) {
                        releaseOccupiedResources(queryWrapp);
                    } catch (Throwable ignore) {
                        // do nothing
                    }

                    throw e;
                }

                Callback[] emptyListners;
                try (LockGuard guard = new LockGuard(lock)) {
                    releaseOccupiedResources(queryWrapp);
                    emptyListners = getFiringEmptyPoolListners();

                    //COMMENT Миронов В. можно оптимизировать поиск запросов на исполнение если releaseResources будет
                    // возвращать список ресурсов у которых нет активных Query или он был заблокирован на SHARED
                    trySubmitNextAvailableQueryBy(queryWrapp.resources);
                }

                fireEmptyPoolListners(emptyListners);
            });
        } catch (RejectedExecutionException e) {
            releaseOccupiedResources(queryWrapp);
            queryWrapp.future.completeExceptionally(GeneralExceptionBuilder.buildServerShutsDownException());
        } catch (Throwable e) {
            releaseOccupiedResources(queryWrapp);
            throw e;
        }
    }

    private Callback[] getFiringEmptyPoolListners() {
        if (!occupiedResources.isEmpty() || !waitingResources.isEmpty() || emptyPoolListners.isEmpty()) {
            return null;
        }

        Callback[] listners = new Callback[emptyPoolListners.size()];
        emptyPoolListners.toArray(listners);
        return listners;
    }

    private void fireEmptyPoolListners(Callback[] listners) {
        if (listners != null) {
            for (Callback item : listners) {
                item.execute(this);
            }
        }
    }

    private void captureOccupiedResources(QueryWrapper queryWrapp) {
        appendResources(queryWrapp, occupiedResources);
        pushMaintenance(queryWrapp.query.getMaintenanceMarker());
    }

    private void releaseOccupiedResources(QueryWrapper queryWrapp) {
        popMaintenance(queryWrapp.query.getMaintenanceMarker());
        removeResources(queryWrapp, occupiedResources);
    }

    private void captureWaitingResources(QueryWrapper queryWrapp) {
        switch (queryWrapp.query.getPriority()) {
            case LOW:
                ++lowPriorityWaitingQueryCount;
                break;
            case HIGH:
                ++highPriorityWaitingQueryCount;
                break;
        }
        appendResources(queryWrapp, waitingResources);
    }

    private void releaseWaitingResources(QueryWrapper queryWrapp) {
        removeResources(queryWrapp, waitingResources);
        switch (queryWrapp.query.getPriority()) {
            case LOW:
                --lowPriorityWaitingQueryCount;
                break;
            case HIGH:
                --highPriorityWaitingQueryCount;
                break;
        }
    }

    private void trySubmitNextAvailableQueryBy(Map<String, LockType> releasedResources) {
        HashSet<QueryWrapper> candidates = new HashSet<>();

        for (Map.Entry<String, LockType> res : releasedResources.entrySet()) {
            ArrayList<QueryLockType> value = waitingResources.get(res.getKey());
            if (value != null) {
                value.forEach(item -> candidates.add(item.query));
            }
        }

        for (QueryWrapper<?> query : candidates) {
            if (isOccupiedResources(query.resources)) {
                continue;
            }

            if (isFilledThreadPool()) {
                break;
            }

            releaseWaitingResources(query);
            submitQuery(query);
        }
    }

    private boolean isOverloaded(Priority newQueryPriority) {
        if (isFilledThreadPool()) {
            return true;
        }

        switch (newQueryPriority) {
            case LOW:
                //В случа низкоприоритетных запросов смотрим так же и на очередь высокоприоритетных - и если там растет нагрузка,
                // то низкоприоритетные мы сразу откидываем
                return (
                        //Аналог highPriorityWaitingQueryCount >= (MAX_WAITING_HIGH_QUERY_COUNT/4)
                        (highPriorityWaitingQueryCount >= (MAX_WAITING_HIGH_QUERY_COUNT >> 2)) ||
                                (lowPriorityWaitingQueryCount >= MAX_WAITING_LOW_QUERY_COUNT)
                );
            case HIGH:
                return highPriorityWaitingQueryCount >= MAX_WAITING_HIGH_QUERY_COUNT;
        }
        return false;
    }

    private boolean isFilledThreadPool() {
        return threadPool.getQueue().size() >= MAX_WORKED_QUERY_COUNT;
    }

    private void pushMaintenance(String marker) {
        if (marker != null) {
            maintenanceMarkers.add(marker);
        }
    }

    private void popMaintenance(String marker) {
        if (marker != null) {
            maintenanceMarkers.remove(maintenanceMarkers.size() - 1);
        }
    }

    private boolean isMaintenance() {
        return !maintenanceMarkers.isEmpty();
    }

    private boolean isOccupiedResources(final Map<String, LockType> targetResources) {
        for (HashMap.Entry<String, LockType> res : targetResources.entrySet()) {
            ArrayList<QueryLockType> foundValue = occupiedResources.get(res.getKey());
            if (foundValue == null || foundValue.isEmpty()) {
                continue;
            }

            if (res.getValue() == LockType.EXCLUSIVE || foundValue.get(0).lockType == LockType.EXCLUSIVE) {
                return true;
            }
        }
        return false;
    }

    public int getHighPriorityWaitingQueryCount() {
        return highPriorityWaitingQueryCount;
    }

    public int getLowPriorityWaitingQueryCount() {
        return lowPriorityWaitingQueryCount;
    }

    private static void appendResources(QueryWrapper<?> query, ResourceMap destination) {
        for (Map.Entry<String, LockType> entry : query.resources.entrySet()) {
            ArrayList<QueryLockType> foundValue = destination.get(entry.getKey());
            if (foundValue == null) {
                foundValue = new ArrayList<>();
                destination.put(entry.getKey(), foundValue);
            }

            foundValue.add(new QueryLockType(query, entry.getValue()));
        }
    }

    private static void removeResources(QueryWrapper<?> query, ResourceMap destination) {
        for (Map.Entry<String, LockType> entry : query.resources.entrySet()) {
            ArrayList<QueryLockType> foundValue = destination.get(entry.getKey());
            if (foundValue == null) {
                continue;
            }

            foundValue.removeIf(item -> item.query == query);
            if (foundValue.isEmpty()) {
                destination.remove(entry.getKey());
            }
        }
    }

    private PlatformException createMaintenanceException() {
        return GeneralExceptionBuilder.buildServerBusyException(maintenanceMarkers.get(maintenanceMarkers.size() - 1));
    }

    private PlatformException createOverloadedException() {
        if (isMaintenance()) {
            return createMaintenanceException();
        }

        return GeneralExceptionBuilder.buildServerOverloadedException();
    }
}
