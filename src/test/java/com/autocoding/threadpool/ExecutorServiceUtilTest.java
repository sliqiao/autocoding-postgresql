package com.autocoding.threadpool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
	 * execute()方法向线程池提交Runnable任务测试
	 */
	@Test
	public void test_runnable_executed() throws InterruptedException {
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(10, 100);
		for (int i = 1; i <= 30; i++) {
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}

				}
			};
			executorService.execute(runnable);

		}

		TimeUnit.MINUTES.sleep(10);
	}

	/**
	 * 
	 * submit（）方法 向线程池提交Runnable任务测试
	 */
	@Test
	public void test_runnable() throws InterruptedException {
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

	/**
	 * 
	 *  submit（）方法向线程池提交Runnable任务测试(任务执行模拟异常)
	 */
	@Test
	public void test_runnable_exception() throws InterruptedException {
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
					throw new RuntimeException("异常测试1");
				}
			});
			// 如果任务执行发生异常，必须要调用future.get()才能抛出异常，否则是看不见异常信息的
			final Future<?> future = executorService.submit(runnableWrapper);

			/**
			 * try {
				future.get();
			} catch (final ExecutionException e) {
				ExecutorServiceUtilTest.log.error("future异常:", e);
			}*/

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
		for (int i = 1; i <= 30; i++) {
			id = "task【" + i + "】";
			final CallableWrapper<Integer> callable = new CallableWrapper<>(id,
					new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					try {
								TimeUnit.SECONDS.sleep(15);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					return counter.getAndIncrement();
				}
			});

			executorService.submit(callable);
		}

		TimeUnit.MINUTES.sleep(10);
	}

	/**
	 * 
	 * 向线程池提交多个Callable任务,invokeAll()方法，此方法当这多个任务全部执行完成，此方法才返回，否则会一直阻塞
	 */
	@Test
	public void test_invokeAll() throws InterruptedException {
		final AtomicInteger counter = new AtomicInteger();
		final List<Callable<Integer>> callableList = new LinkedList<>();
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(2, 100);
		String id = null;
		for (int i = 1; i <= 5; i++) {
			id = "task【" + i + "】";
			final CallableWrapper<Integer> callable = new CallableWrapper<>(id,
					new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					try {
						TimeUnit.SECONDS.sleep(10 + counter.get());
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					return counter.getAndIncrement();
				}
			});

			callableList.add(callable);
		}
		final List<Future<Integer>> futureList = executorService.invokeAll(callableList);
		ExecutorServiceUtilTest.log.info("主线程继续......");
		TimeUnit.MINUTES.sleep(10);
	}

	/**
	 * 
	 * 向线程池提交多个Callable任务,invokeAll()方法，此方法当这多个任务全部执行完成，此方法才返回，否则会一直阻塞
	 */
	@Test
	public void test_invokeAny() throws InterruptedException {
		final AtomicInteger counter = new AtomicInteger();
		final List<Callable<Integer>> callableList = new LinkedList<>();
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(2, 100);
		String id = null;
		for (int i = 1; i <= 1; i++) {
			id = "task【" + i + "】";
			final CallableWrapper<Integer> callable = new CallableWrapper<>(id,
					new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					try {
						TimeUnit.SECONDS.sleep(10 + counter.get());
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					return counter.getAndIncrement();
				}
			});

			callableList.add(callable);
		}
		try {
			final Integer returnValue = executorService.invokeAny(callableList);
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}

		ExecutorServiceUtilTest.log.info("主线程继续......");
		TimeUnit.MINUTES.sleep(10);
	}
}
