package com.autocoding.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class FutureUtil {
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

	private FutureUtil() {
	}

	public static <T> void get(Future<T> future, Callback<T> callback) {

		FutureUtil.EXECUTOR_SERVICE.execute(new Runnable() {

			@Override
			public void run() {
				try {
					final T result = future.get();
					callback.sucess(result);
				} catch (final InterruptedException e) {
					callback.onError(e);
				} catch (final ExecutionException e) {
					callback.onError(e);
				}
			}
		});

	}

	public static interface Callback<T> {
		T sucess(T result);

		void onError(Throwable throwable);
	}
}
