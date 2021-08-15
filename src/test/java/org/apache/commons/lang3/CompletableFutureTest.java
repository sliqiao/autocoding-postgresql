package org.apache.commons.lang3;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.collect.Lists;

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
public class CompletableFutureTest {

	private enum ResultStatus {
		SUCESSFUL, FAILING, CANCELLED
	}

	@Test
	public void test() throws ParseException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(3);
		final List<MyTask> taskList = Lists.newArrayList();
		final MyTask t1 = new MyTask("T1", 10, ResultStatus.FAILING);
		final MyTask t2 = new MyTask("T2", 5, ResultStatus.FAILING);
		final MyTask t3 = new MyTask("T3", 30, ResultStatus.SUCESSFUL);
		taskList.add(t1);
		taskList.add(t2);
		taskList.add(t3);
		for (final MyTask task : taskList) {
			CompletableFuture.supplyAsync(() -> task.run())
			.thenAccept((result) -> this.callback(result, task, taskList, countDownLatch));
		}
		countDownLatch.await();
	}

	private void callback(ResultStatus result, MyTask currentTask, List<MyTask> taskList,
			CountDownLatch countDownLatch) {

		if (result == ResultStatus.FAILING) {
			for (final MyTask tempMyTask : taskList) {
				if (tempMyTask == currentTask) {
					continue;
				}
				tempMyTask.cancel();
			}
		}
		countDownLatch.countDown();
	}

	private class MyTask {
		private volatile int cancelState = MyTask.UNCANCELLED;
		private final String taskName;
		private final int executionTimeInMS;
		private final ResultStatus resultStatus;
		private Thread currentThread;
		private static final int CANCELLING = 1;
		private static final int CANCELLED = 2;
		private static final int UNCANCELLED = 3;

		public MyTask(String taskName, int executionTimeInMS, ResultStatus resultStatus) {
			this.taskName = taskName;
			this.executionTimeInMS = executionTimeInMS;
			this.resultStatus = resultStatus;
		}

		public ResultStatus run() {
			try {
				this.currentThread = Thread.currentThread();
				TimeUnit.SECONDS.sleep(this.executionTimeInMS);
				if (ResultStatus.SUCESSFUL == this.resultStatus) {
					CompletableFutureTest.log.info("【{}】执行成功", this.taskName);
				} else if (ResultStatus.FAILING == this.resultStatus) {
					CompletableFutureTest.log.error("【{}】执行失败", this.taskName);
				}

			} catch (final InterruptedException e) {
				CompletableFutureTest.log.error("【{}】响应中断", this.taskName);
			}

			return this.resultStatus;
		}

		/**
		 * cancel()方法要保证线程安全，同时，要保证幂等
		 */
		public void cancel() {
			if (this.resultStatus == ResultStatus.FAILING) {
				//CompletableFutureTest.log.info("【{}】执行失败，不需要取消", this.taskName);
				return;
			}
			if (this.cancelState == MyTask.CANCELLING) {
				CompletableFutureTest.log.info("【{}】正在取消，不需要再取消了", this.taskName);
				return;
			}
			if (this.cancelState == MyTask.CANCELLED) {
				return;
			}
			synchronized (this) {
				if (this.cancelState == MyTask.UNCANCELLED) {
					CompletableFutureTest.log.info("【{}】开始进行业务取消逻辑 :开始", this.taskName);
					//TODO 任务取消逻辑
					//IO型任务，可以通过中断
					this.currentThread.interrupt();
					CompletableFutureTest.log.info("【{}】开始进行业务取消逻辑 :结束", this.taskName);
					this.cancelState = MyTask.CANCELLED;
				}
			}
		}
	}

}
