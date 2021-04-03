package com.autocoding.threadpool;

import java.util.concurrent.Callable;

/**
 * RunnableId 工具类
 *
 * @ClassName: RunnableIdUtil
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:10:48 PM
 */
final class RunnableUtil {
	private RunnableUtil() {

	}

	/**
	 * 从主调线程上下文中获取TaskId，并立即清除
	 * @param runnable
	 * @return
	 */
	public static String getTaskIdAndClear(Runnable runnable) {
		String runnableId = String.valueOf(runnable.hashCode());
		if (null != RunnableContext.getId()) {
			runnableId = RunnableContext.getId();
			RunnableContext.removeId();
		}
		return runnableId;

	}

	/**
	 * 从主调线程上下文中获取TaskId，并立即清除
	 * @param callable
	 * @return
	 */
	public static String getTaskIdAndClear(Callable<?> callable) {
		String callableId = String.valueOf(callable.hashCode());
		if (null != RunnableContext.getId()) {
			callableId = RunnableContext.getId();
			RunnableContext.removeId();
		}
		return callableId;

	}

	/**
	 * 从主调线程上下文中获取TaskName，并立即清除
	 * @param runnable
	 * @return
	 */
	public static String getTaskNameAndClear() {
		String taskName = null;
		if (null != RunnableContext.getName()) {
			taskName = RunnableContext.getName();
			RunnableContext.removeName();
		}
		return taskName;

	}


	/**
	 * 从主调线程上下文中获取TaskId，并立即清除
	 * @param runnable
	 * @return
	 */
	public static String getTaskId(Runnable runnable) {
		String taskId = null;
		if (runnable instanceof RunnableWrapper) {
			taskId = ((RunnableWrapper) runnable).getId();
		}
		if (null == taskId) {
			taskId = String.valueOf(runnable.hashCode());
		}
		return taskId;

	}
}
