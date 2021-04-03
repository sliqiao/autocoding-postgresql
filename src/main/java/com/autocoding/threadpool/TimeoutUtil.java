package com.autocoding.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;

/**
 * 超时工具类 
 *
 */
@Slf4j
public final class TimeoutUtil {
	private static ExecutorService DEFAULT_EXECUTOR_SERVICE = new ThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors() * 2,
			Runtime.getRuntime().availableProcessors() * 2, 30, TimeUnit.MINUTES,
			new LinkedBlockingDeque<>(1000),
			new MonitoredThreadPoolExecutor.MyThreadFactory("timeout"),
			new ThreadPoolExecutor.CallerRunsPolicy());

	public static <T> T execute(Runnable runnable, T result, long timeout, TimeUnit timeoutUnit) {

		final FutureTask<T> futureTask = new FutureTask<T>(runnable, result);
		TimeoutUtil.DEFAULT_EXECUTOR_SERVICE.submit(futureTask);
		try {
			result = futureTask.get(timeout, timeoutUnit);
		} catch (final InterruptedException e) {
			TimeoutUtil.log.error("中断异常", e);
		} catch (final ExecutionException e) {
			TimeoutUtil.log.error("执行异常", e);
		} catch (final TimeoutException e) {
			TimeoutUtil.log.error("超时异常");
			futureTask.cancel(false);
		} catch (final Exception e) {
			result = null;
		}
		return result;
	}

	public static void execute(Runnable runnable, long timeout, TimeUnit timeoutUnit) {
		final FutureTask<?> futureTask = new FutureTask<Void>(runnable, null);
		TimeoutUtil.DEFAULT_EXECUTOR_SERVICE.submit(futureTask);
		try {
			futureTask.get(timeout, timeoutUnit);
		} catch (final InterruptedException e) {
			TimeoutUtil.log.error("中断异常", e);
		} catch (final ExecutionException e) {
			TimeoutUtil.log.error("执行异常", e);
		} catch (final TimeoutException e) {
			TimeoutUtil.log.error("超时异常");
			futureTask.cancel(false);
		}

	}

	public static void execute(Runnable runnable, long timeoutInSeconds) {
		TimeoutUtil.execute(runnable, timeoutInSeconds, TimeUnit.SECONDS);

	}

}
