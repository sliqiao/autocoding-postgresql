package com.autocoding.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * ExecutorServiceUtil
 */
@Slf4j
public class ExecutorServiceUtilTest {

	@Test
	public void test_RunnableWrapper() throws InterruptedException {
		final AtomicInteger counter = new AtomicInteger(0);
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(10, 100);
		String id = null;
		for (int i = 1; i <= 30; i++) {
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

}
