package com.autocoding.threadpool;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程池工具类
 *
 * @ClassName: ExecutorServiceUtil
 * @author: QiaoLi
 * @date: Aug 27, 2020 8:15:46 AM
 */
@Slf4j
public final class ExecutorServiceUtil {
	private static final int DEFAULT_INIT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	private static final int DEFAULT_MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	private static final int DEFAULT_QUEUE_SIZE = 10000;
	private static final String DEFAULT_POLL_NAME = "Common-Pool";
	private static final String DEFAULT_SCHEDULED_POLL_NAME = "Common-Scheduled-Pool";
	private static ExecutorService DEFAULT_EXECUTOR_SERVICE = MonitoredThreadPoolExecutor.create(ExecutorServiceUtil.DEFAULT_POLL_NAME, ExecutorServiceUtil.DEFAULT_INIT_THREADS,
			ExecutorServiceUtil.DEFAULT_MAX_THREADS, 3600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(ExecutorServiceUtil.DEFAULT_QUEUE_SIZE),
			new MonitoredThreadPoolExecutor.MyThreadFactory(ExecutorServiceUtil.DEFAULT_POLL_NAME), new ThreadPoolExecutor.CallerRunsPolicy());
	private static final ScheduledExecutorService DEFAULT_SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(), new MonitoredThreadPoolExecutor.MyThreadFactory(ExecutorServiceUtil.DEFAULT_SCHEDULED_POLL_NAME));
	private static final Map<String, MonitoredThreadPoolExecutor> EXECUTOR_SERVICE_REGISTRY = new HashMap<String, MonitoredThreadPoolExecutor>();
	private static final AtomicInteger POOL_COUNTER = new AtomicInteger(1);

	static {
		ExecutorServiceUtil.init();
	}

	private ExecutorServiceUtil() {

	}


	public ExecutorService getExecutorService() {
		return ExecutorServiceUtil.DEFAULT_EXECUTOR_SERVICE;
	}


	public ScheduledExecutorService getDEFAULT_SCHEDULED_EXECUTOR_SERVICE() {
		return ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE;
	}

	public static ExecutorService newExecutorService(int maxThread) {
		return ExecutorServiceUtil.newExecutorService(ExecutorServiceUtil.DEFAULT_QUEUE_SIZE, maxThread, new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static ExecutorService newExecutorService(int maxThread, int queueSize) {
		return ExecutorServiceUtil.newExecutorService(queueSize, maxThread, new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static ExecutorService newExecutorService(int workQueueSize, int maxThread,
			RejectedExecutionHandler rejectedExecutionHandler) {
		final String poolName = ExecutorServiceUtil.DEFAULT_POLL_NAME + "-" + ExecutorServiceUtil.POOL_COUNTER.getAndIncrement();
		if (maxThread < ExecutorServiceUtil.DEFAULT_INIT_THREADS) {
			maxThread = ExecutorServiceUtil.DEFAULT_INIT_THREADS;
		}
		final ExecutorService executorService = MonitoredThreadPoolExecutor.create(poolName, ExecutorServiceUtil.DEFAULT_INIT_THREADS, maxThread, 30L,
				TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(workQueueSize),
				new MonitoredThreadPoolExecutor.MyThreadFactory(poolName),
				rejectedExecutionHandler);
		ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.put(poolName, (MonitoredThreadPoolExecutor) executorService);
		return executorService;
	}

	@PreDestroy
	public void destroy() throws Exception {
		ExecutorServiceUtil.log.info("spirng容器关闭时，关闭通用线程池:{}", ExecutorServiceUtil.DEFAULT_POLL_NAME);
		ExecutorServiceUtil.DEFAULT_EXECUTOR_SERVICE.shutdown();
		for (final Entry<String, MonitoredThreadPoolExecutor> e : ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.entrySet()) {
			ExecutorServiceUtil.log.info("spirng容器关闭时，关闭线程池:{}", e.getKey());
			e.getValue().shutdown();
		}
		ExecutorServiceUtil.log.info("spirng容器关闭时，关闭调度线程池");
		ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE.shutdown();

	}

	private static void init() {
		ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					MonitoredThreadPoolExecutorUtil.log(ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.values());
				} catch (final Exception e) {
					ExecutorServiceUtil.log.error("执行MonitoredThreadPoolExecutorUtil.log()异常", e);
				}

			}
		}, 10, 1, TimeUnit.MINUTES);
	}


	public static class MyRejectedExecutionHandler implements RejectedExecutionHandler {
		private final RejectedExecutionHandler rejectedExecutionHandler;

		public MyRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
			this.rejectedExecutionHandler = rejectedExecutionHandler;
		}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			ExecutorServiceUtil.log.info("丢弃策略执行...");
			final String warnningMsg = String.format("告警,线程池满了，任务列队也满了，丢弃任务！");
			ExecutorServiceUtil.log.error(warnningMsg);
			this.rejectedExecutionHandler.rejectedExecution(r, executor);

		}
	}
}
