package com.autocoding.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * CompletionService 测试
 * @author Administrator
 *
 */
@Slf4j
public class CompletionServiceTest {
	public static void main(String[] args) {
		final int taskSize = 5;
		final ExecutorService executor = Executors.newFixedThreadPool(taskSize);
		final CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(
				executor);
		for (int i = 1; i <= taskSize; i++) {
			completionService.submit(new ReturnAfterSleepCallable(taskSize - i + 1, i));
		}

		for (int i = 0; i < taskSize; i++) {
			try {
				CompletionServiceTest.log.info(completionService.take().get() + "");
			} catch (final InterruptedException e) {
				e.printStackTrace();
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}

		System.out.println("all over.");
		executor.shutdown();
	}

	private static class ReturnAfterSleepCallable implements Callable<Integer> {
		private final int sleepSeconds;
		private final int returnValue;

		public ReturnAfterSleepCallable(int sleepSeconds, int returnValue) {
			this.sleepSeconds = sleepSeconds;
			this.returnValue = returnValue;
		}

		@Override
		public Integer call() throws Exception {
			CompletionServiceTest.log.info("任务编号：{},begin to execute.sleep in {} sencods ",
					this.returnValue, this.sleepSeconds);
			TimeUnit.SECONDS.sleep(this.sleepSeconds);
			CompletionServiceTest.log.info("任务编号：{},end to execute.", this.returnValue);
			return this.returnValue;
		}
	}

}
