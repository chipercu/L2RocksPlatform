package com.fuzzy.main.platform.sdk.threadpool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.utils.DefaultThreadPoolExecutor;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ThreadPool {

    public static final int PREFERRED_TASK_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int MAXIMUM_POOL_SIZE = 2 * PREFERRED_TASK_COUNT;

    private final ThreadPoolExecutor executor;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public ThreadPool(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.executor = new DefaultThreadPoolExecutor(
                MAXIMUM_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                5, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                "ThreadPool",
                uncaughtExceptionHandler);
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.executor.allowCoreThreadTimeOut(true);
    }

    public CompletableFuture<Void> runAsync(Runnable task) {
        return supplyAsync(() -> {
            task.run();
            return null;
        });
    }

    public <T> CompletableFuture<T> supplyAsync(Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                T res = task.get();
                future.complete(res);
            } catch (Throwable e) {
                future.completeExceptionally(new CancellationException());
                throw e;
            }
        });
        return future;
    }

    /**
     * Executes the given tasks, returning when all complete.
     * The current thread is also used to execute the first task.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     */
    public void invokeAll(List<Callable<Void>> tasks) throws PlatformException, CancellationException {
        if (tasks.isEmpty()) {
            return;
        }

        boolean currentThreadException = true;
        try {
            if (tasks.size() == 1) {
                tasks.get(0).call();
                return;
            }

            Future[] futures = new Future[tasks.size() - 1];
            Throwable firstException = null;
            try {
                for (int i = 1; i < tasks.size(); ++i) {
                    futures[i - 1] = executor.submit(tasks.get(i));
                }
                tasks.get(0).call();
            } catch (Throwable e) {
                firstException = e;
            } finally {
                for (Future future : futures) {
                    if (future == null){
                        continue;
                    }

                    try {
                        future.get();
                    } catch (ExecutionException e) {
                        if (firstException == null) {
                            firstException = e.getCause();
                            currentThreadException = false;
                        }
                    } catch (Throwable e) {
                        if (firstException == null) {
                            firstException = e;
                            currentThreadException = false;
                        }
                    }
                }
            }
            if (firstException != null) {
                throw firstException;
            }
        } catch (PlatformException e) {
            throw e;
        } catch (Throwable e) {
            if (currentThreadException) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
            }
            throw new CancellationException();
        }
    }
}
