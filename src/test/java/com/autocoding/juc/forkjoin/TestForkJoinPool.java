package com.autocoding.juc.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import lombok.extern.slf4j.Slf4j;

/**
 * 这是一个简单的Join/Fork计算过程，将1—1001数字相加
 */
@Slf4j
public class TestForkJoinPool {

	private static final Integer MAX = 200;

	static class MyForkJoinTask extends RecursiveTask<Integer> {
		// 子任务开始计算的值
		private final Integer startValue;

		// 子任务结束计算的值
		private final Integer endValue;

		public MyForkJoinTask(Integer startValue, Integer endValue) {
			this.startValue = startValue;
			this.endValue = endValue;
		}

		@Override
		protected Integer compute() {
			// 如果条件成立，说明这个任务所需要计算的数值分为足够小了
			// 可以正式进行累加计算了
			if (this.endValue - this.startValue < TestForkJoinPool.MAX) {
				System.out.println(
						"开始计算的部分：startValue = " + this.startValue + ";endValue = " + this.endValue);
				Integer totalValue = 0;
				for (int index = this.startValue; index <= this.endValue; index++) {
					totalValue += index;
				}
				return totalValue;
			}
			// 否则再进行任务拆分，拆分成两个任务
			else {
				final MyForkJoinTask subTask1 = new MyForkJoinTask(this.startValue,
						(this.startValue + this.endValue) / 2);
				subTask1.fork();
				final MyForkJoinTask subTask2 = new MyForkJoinTask(
						(this.startValue + this.endValue) / 2 + 1, this.endValue);
				subTask2.fork();
				return subTask1.join() + subTask2.join();
			}
		}
	}

	public static void main(String[] args) {
		// 这是Fork/Join框架的线程池
		final ForkJoinPool pool = new ForkJoinPool();
		TestForkJoinPool.log.info("开始计算");
		final ForkJoinTask<Integer> taskFuture = pool.submit(new MyForkJoinTask(1, 10001));
		try {
			final Integer result = taskFuture.get();
			System.out.println("result = " + result);

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace(System.out);
		}
		TestForkJoinPool.log.info("结束计算........");

	}
}