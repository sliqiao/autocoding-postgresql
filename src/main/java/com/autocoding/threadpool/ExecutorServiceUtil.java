package com.autocoding.threadpool;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
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
	/** 默认初始线程数 */
	private static final int DEFAULT_INIT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	/** 默认最大始线程数 */
	private static final int DEFAULT_MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	/** 默认工作队列大小 */
	private static final int DEFAULT_QUEUE_SIZE = 10000;
	/** 默认线程池名称*/
	private static final String DEFAULT_POLL_NAME = "Common-Pool";
	/** 默认可调度线程池名称*/
	private static final String DEFAULT_SCHEDULED_POLL_NAME = "Common-Scheduled-Pool";
	/** 默认线程池*/
	private static ExecutorService DEFAULT_EXECUTOR_SERVICE = MonitoredThreadPoolExecutor
			.create(ExecutorServiceUtil.DEFAULT_POLL_NAME, ExecutorServiceUtil.DEFAULT_INIT_THREADS,
					ExecutorServiceUtil.DEFAULT_MAX_THREADS, 3600L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(ExecutorServiceUtil.DEFAULT_QUEUE_SIZE),
					new MonitoredThreadPoolExecutor.MyThreadFactory(
							ExecutorServiceUtil.DEFAULT_POLL_NAME),
					new ThreadPoolExecutor.CallerRunsPolicy());
	/** 默认可调度线程池*/
	private static final ScheduledExecutorService DEFAULT_SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(),
			new MonitoredThreadPoolExecutor.MyThreadFactory(
					ExecutorServiceUtil.DEFAULT_SCHEDULED_POLL_NAME));
	/** 线程池注册表*/
	private static final Map<String, MonitoredThreadPoolExecutor> EXECUTOR_SERVICE_REGISTRY = new HashMap<String, MonitoredThreadPoolExecutor>();
	/** 线程池计数器*/
	private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);

	static {
		ExecutorServiceUtil.init();
	}

	private ExecutorServiceUtil() {

	}

	/**	
	 * 获取默认线程池
	 * @return
	 */
	public ExecutorService getExecutorService() {
		return ExecutorServiceUtil.DEFAULT_EXECUTOR_SERVICE;
	}

	/**
	 * 获取默认可调度线程池
	 * @return
	 */
	public ScheduledExecutorService getDefaultScheduledExecutorService() {
		return ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE;
	}

	/**
	 * 
	 * @param maxThread 最大线程数
	 * @return
	 */
	public static ExecutorService newExecutorService(int corePoolSize) {
		return ExecutorServiceUtil.newExecutorService(corePoolSize,
				ExecutorServiceUtil.DEFAULT_MAX_THREADS, ExecutorServiceUtil.DEFAULT_QUEUE_SIZE,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 
	 * @param maximumPoolSize 最大线程数
	 * @param workQueueSize 任务队列大小
	 * @return
	 */
	public static ExecutorService newExecutorService(int corePoolSize, int maximumPoolSize,
			int workQueueSize) {
		return ExecutorServiceUtil.newExecutorService(corePoolSize, maximumPoolSize, workQueueSize,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 
	 * @param maximumPoolSize 最大线程数
	 * @param workQueueSize 任务队列大小
	 * @param isSupportingPriority 是否支持优先级
	 * @return
	 */
	public static ExecutorService newExecutorService(int maximumPoolSize, int workQueueSize,
			boolean isSupportingPriority) {

		final String poolName = ExecutorServiceUtil.genPoolName();
		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(
				ExecutorServiceUtil.DEFAULT_INIT_THREADS, maximumPoolSize, 30L, TimeUnit.MINUTES,
				workQueueSize, new MonitoredThreadPoolExecutor.MyThreadFactory(poolName),
				new ThreadPoolExecutor.CallerRunsPolicy(), isSupportingPriority);
		ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.put(poolName,
				(MonitoredThreadPoolExecutor) executorService);
		return executorService;
	}

	/**
	 * 
	 * @param workQueueSize 任务队列大小
	 * @param corePoolSize 最大线程数
	 * @param maximumPoolSize 最大线程数
	 * @param rejectedExecutionHandler 拒绝执行处理器
	 * @return
	 */
	public static ExecutorService newExecutorService(int corePoolSize, int maximumPoolSize,
			int workQueueSize, RejectedExecutionHandler rejectedExecutionHandler) {

		if (maximumPoolSize < corePoolSize) {
			maximumPoolSize = corePoolSize;
		}
		final String poolName = ExecutorServiceUtil.genPoolName();

		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(
				corePoolSize, maximumPoolSize, 30L, TimeUnit.MINUTES,
				workQueueSize, new MonitoredThreadPoolExecutor.MyThreadFactory(poolName),
				rejectedExecutionHandler, false);
		ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.put(poolName,
				(MonitoredThreadPoolExecutor) executorService);
		return executorService;
	}

	/**
	 * 
	 * @param corePoolSize 核心线程数
	 * @param maximumPoolSize  最大线程数
	 * @param keepAliveTime 线程保活时间：长度
	 * @param timeUnit 线程保活时间：单位
	 * @param workQueueSize 任务队列大小
	 * @param threadFactory 线程工厂
	 * @param rejectedExecutionHandler  拒绝执行处理器
	 * @return
	 */
	public static ExecutorService newExecutorService(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit timeUnit, int workQueueSize, ThreadFactory threadFactory,
			RejectedExecutionHandler rejectedExecutionHandler, boolean isSupportingPriority) {
		final String poolName = ExecutorServiceUtil.genPoolName();
		if (maximumPoolSize < corePoolSize) {
			maximumPoolSize = corePoolSize;
		}
		BlockingQueue<Runnable> workQueue = null;
		if (isSupportingPriority) {
			//注意PriorityBlockingQueue是一个无界队列
			workQueue = new PriorityBlockingQueue<Runnable>(workQueueSize);

		} else {
			workQueue = new LinkedBlockingQueue<Runnable>(workQueueSize);
		}
		final ExecutorService executorService = MonitoredThreadPoolExecutor.create(poolName,
				corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue,
				new MonitoredThreadPoolExecutor.MyThreadFactory(poolName),
				rejectedExecutionHandler);
		ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.put(poolName,
				(MonitoredThreadPoolExecutor) executorService);
		return executorService;
	}

	@PreDestroy
	public void destroy() throws Exception {
		ExecutorServiceUtil.log.info("spirng容器关闭时，关闭通用线程池:{}",
				ExecutorServiceUtil.DEFAULT_POLL_NAME);
		ExecutorServiceUtil.DEFAULT_EXECUTOR_SERVICE.shutdown();
		for (final Entry<String, MonitoredThreadPoolExecutor> e : ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY
				.entrySet()) {
			ExecutorServiceUtil.log.info("spirng容器关闭时，关闭线程池:{}", e.getKey());
			e.getValue().shutdown();
		}
		ExecutorServiceUtil.log.info("spirng容器关闭时，关闭调度线程池");
		ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE.shutdown();

	}

	/**
	 *生成线程池名称
	 */
	private static String genPoolName() {
		final String poolName = ExecutorServiceUtil.DEFAULT_POLL_NAME + "-"
				+ ExecutorServiceUtil.POOL_COUNTER.getAndIncrement();
		return poolName;
	}

	private static void init() {
		ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE
		.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					MonitoredThreadPoolExecutorUtil
					.log(ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY.values());
				} catch (final Exception e) {
					ExecutorServiceUtil.log
					.error("执行MonitoredThreadPoolExecutorUtil.log()异常", e);
				}

			}
		}, 10, 1, TimeUnit.MINUTES);
	}

}
