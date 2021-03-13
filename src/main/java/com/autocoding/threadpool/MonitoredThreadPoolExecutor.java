package com.autocoding.threadpool;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 可监控的线程池，可以监控任务的排队时间、任务执行时间、线程池当前状态
 *
 * @ClassName: MonitoredThreadPoolExecutor
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:10:48 PM
 */
@Slf4j
public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor {
	/**
	 * 线程池中任务执行超时：毫秒
	 */
	private static final int EXECUTION_TIME_OUT_IN_MS = 1000 * 20;

	private String poolName;
	/**
	 * key=runnableId , value=RunnableStatInfo
	 */
	private final Map<String, TaskStatInfo> runnableStatInfoMap;
	/**
	 * key=futureTask的hashCode , value=runnable的id
	 */
	private final Map<Integer, String> idMap;

	private MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit timeUnit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory,
				handler);
		this.runnableStatInfoMap = new ConcurrentHashMap<String, TaskStatInfo>(
				maximumPoolSize * 10);
		this.idMap = new ConcurrentHashMap<Integer, String>(maximumPoolSize * 10);

	}

	/**
	 * 静态工厂方法：创建可监控的线程池
	 *
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param timeUnit
	 * @param workQueue
	 * @param threadFactory
	 * @param handler
	 * @return
	 */

	public static ExecutorService create(String poolName, int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workQueue,
			ThreadFactory threadFactory, RejectedExecutionHandler handler) {

		final MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor(
				corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory,
				handler);
		monitoredThreadPoolExecutor.setPoolName(poolName);
		return monitoredThreadPoolExecutor;

	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		if (callable instanceof RunnableWrapper) {
			final RunnableWrapper runnableWrapper = (RunnableWrapper) callable;
			final TaskStatInfo runnableStatInfo = new TaskStatInfo();
			runnableStatInfo.setId(runnableWrapper.getId());
			runnableStatInfo.setOriHashCode(runnableWrapper.getRunnable().hashCode());
			runnableStatInfo.setDesc(runnableWrapper.getDesc());
			runnableStatInfo.setSubmittedTime(new Date());
			this.runnableStatInfoMap.put(runnableStatInfo.getId(), runnableStatInfo);
		} else if (callable instanceof CallableWrapper) {
			final CallableWrapper<T> callableWrapper = (CallableWrapper<T>) callable;
			final TaskStatInfo runnableStatInfo = new TaskStatInfo();
			runnableStatInfo.setId(callableWrapper.getId());
			runnableStatInfo.setOriHashCode(callableWrapper.getCallable().hashCode());
			runnableStatInfo.setDesc(callableWrapper.getDesc());
			runnableStatInfo.setSubmittedTime(new Date());
			this.runnableStatInfoMap.put(runnableStatInfo.getId(), runnableStatInfo);
		} else {
			MonitoredThreadPoolExecutor.log.error("非法类型,task:{}", callable.getClass().getName());
		}
		return super.newTaskFor(callable);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {

		final String runnableId = this.getRunnableId(r);
		this.idMap.put(r.hashCode(), runnableId);
		TaskStatInfo runnableStatInfo = this.runnableStatInfoMap.get(runnableId);
		if (null != runnableStatInfo) {
			runnableStatInfo.setStartTime(new Date());
		} else {
			// ExecutorService.execute(Runnable)提交的任务，不经过newTaskFor（）方法，所以需要在这里初始化TaskStatInfo对象
			runnableStatInfo = new TaskStatInfo();
			runnableStatInfo.setId(runnableId);
			runnableStatInfo.setOriHashCode(r.hashCode());
			runnableStatInfo.setSubmittedTime(new Date());
			runnableStatInfo.setStartTime(new Date());
			this.runnableStatInfoMap.put(runnableStatInfo.getId(), runnableStatInfo);
		}

		MonitoredThreadPoolExecutor.log.info("RunnableId:{},开始执行！ ", runnableId);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		Long waitingTimeInMs = null;
		Long elapsedTimeInMs = null;
		Long totalTimeInMs = null;
		final String runnableId = this.idMap.get(r.hashCode());
		if (null != runnableId) {
			final TaskStatInfo runnableStatInfo = this.runnableStatInfoMap.get(runnableId);
			if (null != runnableStatInfo) {
				final Date startTime = runnableStatInfo.getStartTime();
				final Date endTime = new Date();
				if (null != runnableStatInfo.getSubmittedTime()) {
					waitingTimeInMs = Long.valueOf(
							startTime.getTime() - runnableStatInfo.getSubmittedTime().getTime());
				}
				elapsedTimeInMs = Long.valueOf(endTime.getTime() - startTime.getTime());
				totalTimeInMs = Long
						.valueOf(endTime.getTime() - runnableStatInfo.getSubmittedTime().getTime());
			}

		}

		if (null != t) {
			MonitoredThreadPoolExecutor.log.error(
					"RunnableId:{},结束执行【异常】, "
							+ "执行耗时: {} ms, ActiveThreadNum: {}, CurrentPoolSize: {}, CorePoolSize: {},MaximumPoolSize: {},LargestPoolSize: {},Task-Completed: {},"
							+ "Task-In-Queue: {}, Task-Total: {},  Thead-KeepAliveTime: {}s,throwable:",
							runnableId, elapsedTimeInMs, this.getActiveCount(), this.getPoolSize(),
							this.getCorePoolSize(), this.getMaximumPoolSize(), this.getLargestPoolSize(),
							this.getCompletedTaskCount(), this.getQueue().size(), this.getTaskCount(),
							this.getKeepAliveTime(TimeUnit.SECONDS), t.getMessage());
		} else {
			if (totalTimeInMs > MonitoredThreadPoolExecutor.EXECUTION_TIME_OUT_IN_MS) {
				MonitoredThreadPoolExecutor.log.warn(
						"RunnableId:{},结束执行【执行超时】, "
								+ "等待时间:{}, 执行耗时: {} ms, ActiveThreadNum: {}, CurrentPoolSize: {}, CorePoolSize: {},MaximumPoolSize: {},LargestPoolSize: {},Task-Completed: {},"
								+ "Task-In-Queue: {}, Task-Total: {},  Thead-KeepAliveTime: {}s",
								runnableId, waitingTimeInMs, elapsedTimeInMs, this.getActiveCount(),
								this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize(),
								this.getLargestPoolSize(), this.getCompletedTaskCount(),
								this.getQueue().size(), this.getTaskCount(),
								this.getKeepAliveTime(TimeUnit.SECONDS));
			} else {
				MonitoredThreadPoolExecutor.log.info(
						"RunnableId:{},结束执行, "
								+ "等待时间:{}, 执行耗时: {} ms, ActiveThreadNum: {}, CurrentPoolSize: {}, CorePoolSize: {},MaximumPoolSize: {},LargestPoolSize: {},Task-Completed: {},"
								+ "Task-In-Queue: {}, Task-Total: {},  Thead-KeepAliveTime: {}s",
						runnableId, waitingTimeInMs, elapsedTimeInMs, this.getActiveCount(),
						this.getPoolSize(),
								this.getCorePoolSize(), this.getMaximumPoolSize(),
								this.getLargestPoolSize(), this.getCompletedTaskCount(),
								this.getQueue().size(), this.getTaskCount(),
								this.getKeepAliveTime(TimeUnit.SECONDS));
			}

		}

	}

	@Override
	public void shutdown() {
		MonitoredThreadPoolExecutor.log.info(
				"ThreadPool Going to shutdown. Executed tasks: {}, Running tasks: {}, Pending tasks: {}",
				this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
		super.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		MonitoredThreadPoolExecutor.log.info(
				"ThreadPool Going to immediately shutdown. Executed tasks: {}, Running tasks: {}, Pending tasks: {}",
				this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
		return super.shutdownNow();
	}

	public String getPoolName() {
		return this.poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * 获取任务Id
	 * java.util.concurrent.ExecutorCompletionService$QueueingFuture 应用程序不可见
	 * java.util.concurrent.Executors$RunnableAdapter 应用程序不可见
	 * @param r
	 * @return
	 */
	private String getRunnableId(Runnable r) {
		String runnableId = String.valueOf(r.hashCode());
		if (r instanceof FutureTask) {
			FutureTask<?> futureTask = (FutureTask<?>) r;
			try {
				// final Field field =
				// futureTask.getClass().getField("callable");
				// field.setAccessible(true);
				// final Object callableObject = field.get(futureTask);
				final Object callableObject = BeanUtil.getProperty(futureTask, "callable");
				if (callableObject instanceof RunnableWrapper) {
					final RunnableWrapper runnableWrapper = (RunnableWrapper) callableObject;
					runnableId = runnableWrapper.getId();
				} else if (callableObject instanceof CallableWrapper) {
					final CallableWrapper<?> callableWrapper = (CallableWrapper<?>) callableObject;
					runnableId = callableWrapper.getId();
				} else if (callableObject.getClass().getSimpleName().equals("RunnableAdapter")) {
					futureTask = BeanUtil.getProperty(callableObject, "task");
					// TODO 请注意，这里使用了递归，有死循环风险
					return this.getRunnableId(futureTask);
				}
			} catch (final Exception e) {
				MonitoredThreadPoolExecutor.log.error("执行getRunnableId()异常", e);

			}
		}
		return runnableId;

	}

	public static class MyThreadFactory implements ThreadFactory {
		private final AtomicLong counter = new AtomicLong(1);
		private final String poolName;

		public MyThreadFactory(String poolName) {
			this.poolName = poolName;
		}

		public String getPoolName() {
			return this.poolName;
		}

		@Override
		public Thread newThread(Runnable r) {
			this.counter.getAndIncrement();
			final Thread newThread = new Thread(r,
					this.poolName + "-" + r.getClass().getSimpleName() + "-" + this.counter.get());
			newThread.setPriority(Thread.NORM_PRIORITY);
			newThread.setDaemon(true);
			newThread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
			return newThread;
		}

		private static class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				MonitoredThreadPoolExecutor.log.error("未捕获异常:", e);

			}
		}

	}

}
