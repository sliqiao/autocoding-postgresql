package com.autocoding.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Administrator
 *
 */
@Slf4j
public class ExecutorServiceUtilTest {
	/**
	 * 
	 * 向线程池提交Runnable任务测试
	 */
	@Test
	public void test_runnable() throws InterruptedException {
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(10, 100);
		String id = null;
		for (int i = 1; i <= 1; i++) {
			id = "task【" + i + "】";
			final RunnableWrapper runnableWrapper = RunnableWrapper.newInstance(id, new Runnable() {
				@Override
				public void run() {

					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}

				}
			});
			executorService.submit(runnableWrapper);
		}

		TimeUnit.MINUTES.sleep(10);
	}

	/**
	 * 
	 * 向线程池提交Callable任务测试
	 */
	@Test
	public void test_callable() throws InterruptedException {
		final AtomicInteger counter = new AtomicInteger();
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(10, 100);
		String id = null;
		for (int i = 1; i <= 1; i++) {
			id = "task【" + i + "】";
			final CallableWrapper<Integer> callable = new CallableWrapper<>(id,
					new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {

					return counter.getAndIncrement();
				}
			});

			executorService.submit(callable);
		}

		TimeUnit.MINUTES.sleep(10);
	}
}
