package com.fuzzy.platform.querypool;

import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class QueryFuture<T> {

	protected final com.fuzzy.platform.querypool.QueryPool queryPool;
	protected final Component component;
	protected final ContextTransaction context;
	private final CompletableFuture<T> future;

	public QueryFuture(QueryPool queryPool, Component component, ContextTransaction context, CompletableFuture<T> future) {
		this.queryPool = queryPool;
		this.component = component;
		if (context == null) {
			this.context = new ContextTransactionImpl(new SourceSystemImpl(), null);
		} else {
			this.context = context;
		}
		this.future = future;
	}

	protected void complete(T result) {
		future.complete(result);
	}

	protected void completeExceptionally(Throwable ex) {
		future.completeExceptionally(ex);
	}

	protected void cancel(boolean mayInterruptIfRunning) {
		future.cancel(mayInterruptIfRunning);
	}

	public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return future.exceptionally(fn);
	}

	public CompletableFuture<Void> thenRun(Runnable action) {
		return future.thenRun(action);
	}

	public <U> CompletableFuture<U> thenApplyCompletableFuture(Function<? super T, ? extends U> fn) {
		return future.thenApply(fn);
	}

	public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return future.whenComplete(action);
	}

	public <U> QueryFuture<U> thenApply(Function<? super T, com.fuzzy.platform.querypool.Query<? extends U>> fn) {
		return thenApply(fn, true);
	}

	public <U> QueryFuture<U> thenApply(Function<? super T, com.fuzzy.platform.querypool.Query<? extends U>> fn, boolean failIfPoolBusy) {
		QueryFuture<U> queryFuture = new QueryFuture<U>(queryPool, component, context, new CompletableFuture<>());
		future.thenApply(t -> {
			Query nextQuery = fn.apply(t);
			queryPool.execute(queryFuture, nextQuery, failIfPoolBusy);
			return null;
		}).exceptionally(throwable -> {
			queryFuture.future.completeExceptionally((Throwable) throwable);
			return null;
		});
		return queryFuture;
	}

	public T get() throws ExecutionException, InterruptedException {
		return future.get();
	}

	public T join() {
		return future.join();
	}
}
