package com.autocoding.threadpool;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.autocoding.threadpool.TaskStatInfo.State;
import com.autocoding.threadpool.warn.DingDingMsg;
import com.autocoding.threadpool.warn.DingDingMsgService;
import com.autocoding.threadpool.warn.DingDingMsgServiceImpl;
import com.autocoding.threadpool.warn.RequestSourceEnum;

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
	/** 默认保留任务信息时间长度：分钟  */
	private static final int DEFAULT_KEEP_TIME_IN_MINUTES_FOR_TASKINFO = 60;
	/**
	 * 线程池中任务执行超时：毫秒
	 */
	private static final int EXECUTION_TIME_OUT_IN_MS = 1000 * 20;

	private String poolName;
	/**
	 * key=taskId , value=RunnableStatInfo
	 */
	private final Map<String, TaskStatInfo> taskStatInfoMap;
	/**
	 * key=futureTask的hashCode , value=taskId
	 */
	private final Map<Integer, String> taskIdMap;
	/** 等待执行的任务taskId集合 */
	private final Set<String> watingTaskIdSet = new CopyOnWriteArraySet<>();
	/** 正在执行的任务taskId集合 */
	private final Set<String> executingTaskIdSet = new CopyOnWriteArraySet<>();
	/** 已经完成的任务taskId集合 */
	private final Set<String> completedTaskIdSet = new CopyOnWriteArraySet<>();
	/**被拒绝执行的任务taskId集合 */
	private final Set<String> rejectedTaskIdSet = new CopyOnWriteArraySet<>();
	/** key=taskId , value=State  */
	private static final Map<String, TaskStatInfo.State> TASK_ID_STATE_MAP = new ConcurrentHashMap<>();

	/** key=taskId , value=State  */
	public static final DelayQueue<DeleteTaskStateDelayedModel<String>> DELAY_QUEUE_OF_DELETE_TASK_STATE = new DelayQueue<>();

	private MonitoredThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit timeUnit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory,
				handler);
		this.taskStatInfoMap = new ConcurrentHashMap<String, TaskStatInfo>(maximumPoolSize * 10);
		this.taskIdMap = new ConcurrentHashMap<Integer, String>(maximumPoolSize * 10);

	}

	public String getPoolName() {
		return this.poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public Set<String> getWatingTaskIdSet() {
		return this.watingTaskIdSet;
	}

	public Set<String> getExecutingTaskIdSet() {
		return this.executingTaskIdSet;
	}

	public Set<String> getCompletedTaskIdSet() {
		return this.completedTaskIdSet;
	}

	public Set<String> getRejectedTaskIdSet() {
		return this.rejectedTaskIdSet;
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
		final MyRejectedExecutionHandler wrappedHandler = new MonitoredThreadPoolExecutor.MyRejectedExecutionHandler(
				handler);
		final MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor(
				corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory,
				wrappedHandler);
		wrappedHandler.setMonitoredThreadPoolExecutor(monitoredThreadPoolExecutor);
		monitoredThreadPoolExecutor.setPoolName(poolName);
		//预启动核心线程
		monitoredThreadPoolExecutor.prestartCoreThread();
		return monitoredThreadPoolExecutor;

	}

	//submit(Callable<T>)、invokeAll(Collection<? extends Callable<T>)提交任务的调用点
	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		final RunnableFuture<T> runnableFuture = super.newTaskFor(callable);
		final String taskId = RunnableUtil.getTaskIdAndClear(runnableFuture);
		final String taskName = RunnableUtil.getTaskNameAndClear();
		this.taskIdMap.put(runnableFuture.hashCode(), taskId);
		TaskStatInfo runnableStatInfo = this.taskStatInfoMap.get(taskId);
		if (null == runnableStatInfo) {
			runnableStatInfo = new TaskStatInfo();
			runnableStatInfo.setId(taskId);
			runnableStatInfo.setName(taskName);
			runnableStatInfo.setOriHashCode(runnableFuture.hashCode());
			runnableStatInfo.setSubmittedTime(new Date());
			runnableStatInfo.setStartTime(new Date());
			this.taskStatInfoMap.put(taskId, runnableStatInfo);
		}
		this.watingTaskIdSet.add(taskId);
		MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.put(taskId, State.WATING);
		return runnableFuture;
	}

	//execute(Runnable ) 提交任务的调用点/submit(Callable)的执行点
	@Override
	public void execute(Runnable command) {
		String taskId = null;
		String taskName = null;
		if (null != this.taskIdMap.get(command.hashCode())) {
			taskId = this.taskIdMap.get(command.hashCode());
			super.execute(command);
			return;
		} else {
			taskId = RunnableUtil.getTaskIdAndClear(command);
			taskName = RunnableUtil.getTaskNameAndClear();
			final RunnableWrapper runnable = RunnableWrapper.newInstance(taskName, taskId, command);
			TaskStatInfo runnableStatInfo = this.taskStatInfoMap.get(taskId);
			if (null == runnableStatInfo) {
				runnableStatInfo = new TaskStatInfo();
				runnableStatInfo.setId(taskId);
				runnableStatInfo.setName(taskName);
				runnableStatInfo.setOriHashCode(command.hashCode());
				runnableStatInfo.setSubmittedTime(new Date());
				runnableStatInfo.setStartTime(new Date());
				this.taskStatInfoMap.put(taskId, runnableStatInfo);
			}
			this.taskIdMap.put(runnable.hashCode(), taskId);
			this.watingTaskIdSet.add(taskId);
			MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.put(taskId, State.WATING);
			super.execute(runnable);
		}
	}

	@Override
	protected void beforeExecute(Thread thread, Runnable runnable) {

		/*	final String taskId = RunnableUtil.getTaskId(r);
			this.taskIdMap.put(r.hashCode(), taskId);*/
		final String taskId = this.taskIdMap.get(runnable.hashCode());
		String taskName = null;
		TaskStatInfo taskStatInfo = this.taskStatInfoMap.get(taskId);
		if (null != taskStatInfo) {
			taskStatInfo.setStartTime(new Date());
			taskName = taskStatInfo.getName();
		} else {
			// TODO  ExecutorService.execute(Runnable)提交的任务，不经过newTaskFor（）方法，所以需要在这里初始化TaskStatInfo对象
			taskStatInfo = new TaskStatInfo();
			taskStatInfo.setId(taskId);
			taskStatInfo.setOriHashCode(runnable.hashCode());
			taskStatInfo.setSubmittedTime(new Date());
			taskStatInfo.setStartTime(new Date());
			this.taskStatInfoMap.put(taskStatInfo.getId(), taskStatInfo);
		}
		MonitoredThreadPoolExecutor.log.info("TaskName:{},TaskId:{},开始执行！ ", taskName, taskId);
		this.executingTaskIdSet.add(taskId);
		MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.put(taskId, State.EXECUTING);
		this.watingTaskIdSet.remove(taskId);

		super.beforeExecute(thread, runnable);
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		super.afterExecute(runnable, throwable);
		if (null == throwable && runnable instanceof Future<?>) {
			try {
				final Future<?> f = (Future<?>) runnable;
				f.get();
			} catch (final InterruptedException e) {
				MonitoredThreadPoolExecutor.log.error("线程池中断异常", e);
				throwable = e;
			} catch (final ExecutionException e) {
				MonitoredThreadPoolExecutor.log.error("线程池执行异常", e);
				throwable = e;
			} catch (final CancellationException e) {
				MonitoredThreadPoolExecutor.log.error("线程池任务取消异常", e);
				throwable = e.getCause();
			}
		}
		Long waitingTimeInMs = null;
		Long elapsedTimeInMs = null;
		Long totalTimeInMs = null;
		final String taskId = this.taskIdMap.get(runnable.hashCode());
		String taskName = null;
		if (null != taskId) {
			final TaskStatInfo taskStatInfo = this.taskStatInfoMap.get(taskId);
			if (null != taskStatInfo) {
				taskName = taskStatInfo.getName();
				final Date startTime = taskStatInfo.getStartTime();
				final Date endTime = new Date();
				if (null != taskStatInfo.getSubmittedTime()) {
					waitingTimeInMs = Long.valueOf(
							startTime.getTime() - taskStatInfo.getSubmittedTime().getTime());
				}
				elapsedTimeInMs = Long.valueOf(endTime.getTime() - startTime.getTime());
				totalTimeInMs = Long
						.valueOf(endTime.getTime() - taskStatInfo.getSubmittedTime().getTime());
			}

		}

		if (null != throwable) {
			MonitoredThreadPoolExecutor.log.error(
					"TaskName:{},TaskId:{},结束执行【异常】, "
							+ "执行耗时: {} ms, ActiveThreadNum: {}, CurrentPoolSize: {}, CorePoolSize: {},MaximumPoolSize: {},LargestPoolSize: {},Task-Completed: {},"
							+ "Task-In-Queue: {}, Task-Total: {},  Thead-KeepAliveTime: {}s,throwable:",
							taskName, taskId, elapsedTimeInMs, this.getActiveCount(), this.getPoolSize(),
							this.getCorePoolSize(), this.getMaximumPoolSize(), this.getLargestPoolSize(),
							this.getCompletedTaskCount(), this.getQueue().size(), this.getTaskCount(),
							this.getKeepAliveTime(TimeUnit.SECONDS), throwable.getMessage());
		} else {
			if (totalTimeInMs > MonitoredThreadPoolExecutor.EXECUTION_TIME_OUT_IN_MS) {
				MonitoredThreadPoolExecutor.log.warn(
						"TaskName:{},TaskId:{},结束执行【执行超时】, "
								+ "等待时间: {} ms, 执行耗时: {} ms, ActiveThreadNum: {}, CurrentPoolSize: {}, CorePoolSize: {},MaximumPoolSize: {},LargestPoolSize: {},Task-Completed: {},"
								+ "Task-In-Queue: {}, Task-Total: {},  Thead-KeepAliveTime: {}s",
								taskName, taskId, waitingTimeInMs, elapsedTimeInMs, this.getActiveCount(),
								this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize(),
								this.getLargestPoolSize(), this.getCompletedTaskCount(),
								this.getQueue().size(), this.getTaskCount(),
								this.getKeepAliveTime(TimeUnit.SECONDS));
			} else {
				MonitoredThreadPoolExecutor.log.info(
						"TaskName:{},TaskId:{},结束执行, "
								+ "等待时间: {} ms, 执行耗时: {} ms, ActiveThreadNum: {}, CurrentPoolSize: {}, CorePoolSize: {},MaximumPoolSize: {},LargestPoolSize: {},Task-Completed: {},"
								+ "Task-In-Queue: {}, Task-Total: {},  Thead-KeepAliveTime: {}s",
								taskName, taskId, waitingTimeInMs, elapsedTimeInMs, this.getActiveCount(),
								this.getPoolSize(), this.getCorePoolSize(), this.getMaximumPoolSize(),
								this.getLargestPoolSize(), this.getCompletedTaskCount(),
								this.getQueue().size(), this.getTaskCount(),
								this.getKeepAliveTime(TimeUnit.SECONDS));
			}

		}
		//TODO 不再使用的数据清理掉，避免内存溢出
		this.taskIdMap.remove(runnable.hashCode());
		this.taskStatInfoMap.remove(taskId);
		this.executingTaskIdSet.remove(taskId);
		this.completedTaskIdSet.add(taskId);
		MonitoredThreadPoolExecutor.DELAY_QUEUE_OF_DELETE_TASK_STATE
		.add(new DeleteTaskStateDelayedModel<String>(taskId,
				MonitoredThreadPoolExecutor.DEFAULT_KEEP_TIME_IN_MINUTES_FOR_TASKINFO,
				TimeUnit.MINUTES));
		MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.put(taskId, State.COMPLETED);
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

	public static class MyThreadFactory implements ThreadFactory {
		private final AtomicLong counter = new AtomicLong(0);
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
			//MyUncaughtExceptionHandler只能处理由execute()向线程池提交的任务，执行中发生的异常
			newThread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
			return newThread;
		}

		@Slf4j
		private static class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				//MyUncaughtExceptionHandler.log.error("未捕获异常处理器:", e);

			}
		}

	}

	/**
	 * 自定义的拒绝执行策略
	 * @author Administrator
	 *
	 */
	private static class MyRejectedExecutionHandler implements RejectedExecutionHandler {
		private final RejectedExecutionHandler rejectedExecutionHandler;
		private MonitoredThreadPoolExecutor monitoredThreadPoolExecutor;

		public MyRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
			this.rejectedExecutionHandler = rejectedExecutionHandler;
		}

		public void setMonitoredThreadPoolExecutor(
				MonitoredThreadPoolExecutor monitoredThreadPoolExecutor) {
			this.monitoredThreadPoolExecutor = monitoredThreadPoolExecutor;
		}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			final String warnningMsg = String.format("告警,线程池满了，任务列队也满了，拒绝策略执行...");
			String taskId = null;
			if (null != this.monitoredThreadPoolExecutor.taskIdMap.get(r.hashCode())) {
				taskId = this.monitoredThreadPoolExecutor.taskIdMap.get(r.hashCode());
			} else {
				taskId = String.valueOf(r.hashCode());
			}
			MonitoredThreadPoolExecutor.log.error("{}:任务未执行!" + warnningMsg, taskId);
			if (executor instanceof MonitoredThreadPoolExecutor) {
				final MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = (MonitoredThreadPoolExecutor) executor;
				monitoredThreadPoolExecutor.getRejectedTaskIdSet().add(taskId);
				MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.put(taskId, State.REJECTED);
				MonitoredThreadPoolExecutor.DELAY_QUEUE_OF_DELETE_TASK_STATE
				.add(new DeleteTaskStateDelayedModel<String>(taskId,
						MonitoredThreadPoolExecutor.DEFAULT_KEEP_TIME_IN_MINUTES_FOR_TASKINFO,
						TimeUnit.MINUTES));
				monitoredThreadPoolExecutor.getWatingTaskIdSet().remove(taskId);
			}
			this.rejectedExecutionHandler.rejectedExecution(r, executor);
			final String text = String.format("告警,线程池满了，任务列队也满了，丢弃任务:" + taskId);
			final DingDingMsg dingDingMsgForLink = new DingDingMsg(new DingDingMsg.Link(text,
					"线程池丢弃Runnable任务", "http://baidu.com", "http://baidu.com"));
			final DingDingMsgService dingDingMsgService = DingDingMsgServiceImpl.getInstance();
			dingDingMsgService.sent(RequestSourceEnum.SYSTEM_DEPLOYMENT, dingDingMsgForLink);
			this.rejectedExecutionHandler.rejectedExecution(r, executor);
		}
	}

	public void clearTaskIdStatInfo() {
		this.watingTaskIdSet.clear();
		this.executingTaskIdSet.clear();
		this.completedTaskIdSet.clear();
		this.rejectedTaskIdSet.clear();
		MonitoredThreadPoolExecutor.log.info(
				"清除统计数据：watingTaskIdSet、executingTaskIdSet、completedTaskIdSet、rejectedTaskIdSet");
	}

	/**
	 * 清除任务执行状态信息[任务执行状态保留1小时]
	 * @param taskId
	 */
	public static void clearTaskIdStatInfo(String taskId) {
		MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.remove(taskId);
		MonitoredThreadPoolExecutor.log.info("清除任务执行状态数据：taskId：{} ", taskId);
	}

	/**
	 * 查询任务状态 
	 * @param taskId
	 * @return
	 */
	public static State queryTaskState(String taskId) {
		return MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP.get(taskId);
	}

	/**
	 * 查询任务状态 
	 * @return
	 */
	public static Map<String, TaskStatInfo.State> queryTaskState() {
		return MonitoredThreadPoolExecutor.TASK_ID_STATE_MAP;
	}
}
