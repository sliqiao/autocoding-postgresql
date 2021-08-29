package com.github.rholder.retry;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.autocoding.threadpool.ExecutorServiceUtil;

import lombok.extern.slf4j.Slf4j;

public class BlockStrategyTest {

	public static void main(String[] args) throws InterruptedException {
		final BlockStrategy blockStrategy = new MyBlockStrategy();
		blockStrategy.block(1000 * 10);
	}

	@Slf4j
	private static class MyBlockStrategy implements BlockStrategy {

		private static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = ExecutorServiceUtil
				.getDefaultScheduledExecutorService();
		private final Semaphore semaphore = new Semaphore(0);

		@Override
		public void block(long sleepTime) throws InterruptedException {
			MyBlockStrategy.log.info("开始阻塞");
			MyBlockStrategy.SCHEDULED_EXECUTOR_SERVICE.schedule(new Runnable() {

				@Override
				public void run() {
					MyBlockStrategy.this.semaphore.release();

				}
			}, sleepTime, TimeUnit.MILLISECONDS);
			this.semaphore.acquire();
			MyBlockStrategy.log.info("结束阻塞");

		}

	}
}
