package org.apache.commons.lang3;

import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 题目背景：有一个任务T，由3个子任务T1、T2、T3组成，T1、T2、T3执行的时间是随机的，并且不等，现
 * 要求，当T1、T2、T3任务全部执行成功，T执行成功，当任意一个任务执行失败，要求快速返回失败T失败，并且
 * 取消掉，其它任务的执行
 * </p>
 *
 */
@Slf4j
public class ListenableFutureTest {

	@Test
	public void test() throws ParseException, InterruptedException {
		final ExecutorService executorService = Executors.newFixedThreadPool(6);
		final ListeningExecutorService listeningExecutorService = MoreExecutors
				.listeningDecorator(executorService);
		final boolean successfulOrFailingFlag = RandomUtils.nextBoolean();
		ListenableFutureTest.log.info("T的子任务,T1、T2、T3开始提交");
		final ListenableFuture<Integer> t1Future = listeningExecutorService
				.submit(new SubTask("T1", successfulOrFailingFlag, 10));
		final ListenableFuture<Integer> t2Future = listeningExecutorService
				.submit(new SubTask("T2", successfulOrFailingFlag, 3));
		final ListenableFuture<Integer> t3Future = listeningExecutorService
				.submit(new SubTask("T3", successfulOrFailingFlag, 20));
		final CountDownLatch countDownLatch = new CountDownLatch(3);
		//t1任务增加回调处理
		Futures.addCallback(t1Future, new FutureCallback<Integer>() {

			@Override
			public void onSuccess(@Nullable Integer result) {
				ListenableFutureTest.log.info("result:{}", result);
				countDownLatch.countDown();

			}

			@Override
			public void onFailure(Throwable t) {
				ListenableFutureTest.log.error("异常", t);
				t2Future.cancel(false);
				t3Future.cancel(false);
				countDownLatch.countDown();
				countDownLatch.countDown();
				countDownLatch.countDown();

			}
		}, executorService);
		//t2任务增加回调处理
		Futures.addCallback(t2Future, new FutureCallback<Integer>() {

			@Override
			public void onSuccess(@Nullable Integer result) {
				ListenableFutureTest.log.info("result:{}", result);
				countDownLatch.countDown();

			}

			@Override
			public void onFailure(Throwable t) {
				ListenableFutureTest.log.error("异常", t);
				t2Future.cancel(false);
				t3Future.cancel(false);
				countDownLatch.countDown();
				countDownLatch.countDown();
				countDownLatch.countDown();

			}
		}, executorService);
		//t3任务增加回调处理
		Futures.addCallback(t3Future, new FutureCallback<Integer>() {

			@Override
			public void onSuccess(@Nullable Integer result) {
				ListenableFutureTest.log.info("result:{}", result);
				countDownLatch.countDown();

			}

			@Override
			public void onFailure(Throwable t) {
				ListenableFutureTest.log.error("异常", t);
				t2Future.cancel(false);
				t3Future.cancel(false);
				countDownLatch.countDown();
				countDownLatch.countDown();
				countDownLatch.countDown();

			}
		}, executorService);
		//主线程的T任务执行完毕
		countDownLatch.await();
		ListenableFutureTest.log.info("T的子任务,T1、T2、T3执行完毕");
	}

	private class SubTask implements Callable<Integer> {

		private final String taskName;
		private final boolean successfulOrFailingFlag;
		private final Integer secondsOfExecution;

		public SubTask(String taskName, boolean successfulOrFailingFlag,
				Integer secondsOfExecution) {
			this.taskName = taskName;
			this.successfulOrFailingFlag = successfulOrFailingFlag;
			this.secondsOfExecution = secondsOfExecution;
		}

		@Override
		public Integer call() throws Exception {
			TimeUnit.SECONDS.sleep(this.secondsOfExecution);
			if (!this.successfulOrFailingFlag) {
				throw new RuntimeException("测试异常");
			}
			return this.secondsOfExecution;
		}

	}

}
