package com.autocoding.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class FutureUtil {
	private FutureUtil() {
	}

	public static <T> T get(Future<T> future, Callback<T> callback) {
		try {
			final T result = future.get();
			callback.sucess(result);
			return result;
		} catch (final InterruptedException e) {
			callback.onError(e);
		} catch (final ExecutionException e) {
			callback.onError(e);
		}
		return null;
	}

	public static interface Callback<T> {
		T sucess(T result);
		void onError(Throwable throwable);
	}
}
