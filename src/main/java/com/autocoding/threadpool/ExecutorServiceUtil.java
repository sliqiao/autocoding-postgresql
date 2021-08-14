package com.autocoding.threadpool;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.autocoding.threadpool.MonitoredThreadPoolExecutor.Builder;

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
	private static final String DEFAULT_POLL_NAME = "CommonPool";
	/** 默认可调度线程池名称*/
	private static final String DEFAULT_SCHEDULED_POLL_NAME = "CommonScheduledPool";
	/** 线程池注册表*/
	public static final Map<String, MonitoredThreadPoolExecutor> EXECUTOR_SERVICE_REGISTRY = new HashMap<String, MonitoredThreadPoolExecutor>();
	/** 默认线程池*/
	private static ExecutorService DEFAULT_EXECUTOR_SERVICE = ExecutorServiceUtil
			.newExecutorService(ExecutorServiceUtil.DEFAULT_POLL_NAME,
					ExecutorServiceUtil.DEFAULT_INIT_THREADS,
					ExecutorServiceUtil.DEFAULT_MAX_THREADS, 3600L, TimeUnit.SECONDS,
					ExecutorServiceUtil.DEFAULT_QUEUE_SIZE,
					new MonitoredThreadPoolExecutor.MyThreadFactory(
							ExecutorServiceUtil.DEFAULT_POLL_NAME),
					new ThreadPoolExecutor.CallerRunsPolicy());
	/** 默认可调度线程池*/
	private static final ScheduledExecutorService DEFAULT_SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(),
			new MonitoredThreadPoolExecutor.MyThreadFactory(
					ExecutorServiceUtil.DEFAULT_SCHEDULED_POLL_NAME));

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
	public static ExecutorService getExecutorService() {
		return ExecutorServiceUtil.DEFAULT_EXECUTOR_SERVICE;
	}

	/**
	 * 获取默认可调度线程池
	 * @return
	 */
	public static ScheduledExecutorService getDefaultScheduledExecutorService() {
		return ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE;
	}

	/**
	 * 
	 * @param poolName corePoolSize
	 * @param corePoolSize 核心线程数
	 * @return
	 */
	public static ExecutorService newExecutorService(String poolName, int corePoolSize) {
		return ExecutorServiceUtil.newExecutorService(poolName, corePoolSize,
				ExecutorServiceUtil.DEFAULT_MAX_THREADS, ExecutorServiceUtil.DEFAULT_QUEUE_SIZE,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 
	 * @param poolName corePoolSize
	 * @param corePoolSize 核心线程数
	 * @param maximumPoolSize 最大线程数
	 * @param workQueueSize 任务队列大小
	 * @return
	 */
	public static ExecutorService newExecutorService(String poolName, int corePoolSize,
			int maximumPoolSize, int workQueueSize) {
		return ExecutorServiceUtil.newExecutorService(poolName, corePoolSize, maximumPoolSize,
				workQueueSize, new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 
	 * @param workQueueSize 任务队列大小
	 * @param corePoolSize 核心线程数
	 * @param maximumPoolSize 最大线程数
	 * @param rejectedExecutionHandler 拒绝执行处理器
	 * @return
	 */
	public static ExecutorService newExecutorService(String poolName, int corePoolSize,
			int maximumPoolSize, int workQueueSize,
			RejectedExecutionHandler rejectedExecutionHandler) {

		final ExecutorService executorService = ExecutorServiceUtil.newExecutorService(poolName,
				corePoolSize, maximumPoolSize, 30L, TimeUnit.MINUTES, workQueueSize,
				new MonitoredThreadPoolExecutor.MyThreadFactory(poolName),
				rejectedExecutionHandler);
		return executorService;
	}

	/**
	 * 
	 * @param poolName  线程池名称:一般与业务相关
	 * @param corePoolSize 核心线程数
	 * @param maximumPoolSize  最大线程数
	 * @param keepAliveTime 线程保活时间：长度
	 * @param timeUnit 线程保活时间：单位
	 * @param workQueueSize 任务队列大小
	 * @param threadFactory 线程工厂
	 * @param rejectedExecutionHandler  拒绝执行处理器
	 * @return
	 */
	public static ExecutorService newExecutorService(String poolName, int corePoolSize,
			int maximumPoolSize, long keepAliveTime, TimeUnit timeUnit, int workQueueSize,
			ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
		final Builder builder = ExecutorServiceUtil.builder();
		builder.poolName(poolName);
		builder.corePoolSize(corePoolSize).maximumPoolSize(maximumPoolSize);
		builder.keepAliveTime(keepAliveTime).timeUnit(timeUnit);
		builder.workQueue(new LinkedBlockingQueue<Runnable>(workQueueSize));
		builder.threadFactory(new MonitoredThreadPoolExecutor.MyThreadFactory(poolName));
		builder.rejectedExecutionHandler(rejectedExecutionHandler);
		final ExecutorService executorService = builder.build();
		return executorService;
	}

	public static Builder builder() {
		return new MonitoredThreadPoolExecutor.Builder();

	}

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
	public static String genPoolName() {
		final String poolName = ExecutorServiceUtil.DEFAULT_POLL_NAME + "-"
				+ ExecutorServiceUtil.POOL_COUNTER.getAndIncrement();
		return poolName;
	}

	private static void init() {
		//周期性的输出统计日志
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
		}, 0, 60, TimeUnit.SECONDS);
		//周期性的清除统计日志数据
		ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE
		.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				for (final MonitoredThreadPoolExecutor e : ExecutorServiceUtil.EXECUTOR_SERVICE_REGISTRY
						.values()) {
					e.clearTaskIdStatInfo();
				}

			}
		}, 0, 1, TimeUnit.HOURS);
		//保留任务执行状态1小时，之后进行删除
		ExecutorServiceUtil.DEFAULT_SCHEDULED_EXECUTOR_SERVICE.submit(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						final String taskId = MonitoredThreadPoolExecutor.DELAY_QUEUE_OF_DELETE_TASK_STATE
								.take().getData();
						MonitoredThreadPoolExecutor.clearTaskIdStatInfo(taskId);
					} catch (final InterruptedException e) {
						ExecutorServiceUtil.log.error("保留任务执行状态1小时，之后进行删除", e);
					}
				}

			}
		});

	}

}
