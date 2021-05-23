package com.autocoding.threadpool;

/**
 * TaskContext 任务上下文
 *
 * @ClassName: TaskContext
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:10:48 PM
 */
public class TaskContext {
	private static ThreadLocal<String> RUNNABLE_ID_THREAD_LOCAL = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return null;
		}

	};
	private static ThreadLocal<String> RUNNABLE_NAME_THREAD_LOCAL = new ThreadLocal<String>() {

		@Override
		protected String initialValue() {
			return null;
		}

	};

	public static void setId(String id) {
		TaskContext.RUNNABLE_ID_THREAD_LOCAL.set(id);
	}

	public static String getId() {
		return TaskContext.RUNNABLE_ID_THREAD_LOCAL.get();
	}

	public static void removeId() {
		TaskContext.RUNNABLE_ID_THREAD_LOCAL.remove();

	}

	public static void setName(String name) {
		TaskContext.RUNNABLE_NAME_THREAD_LOCAL.set(name);
	}

	public static String getName() {
		return TaskContext.RUNNABLE_NAME_THREAD_LOCAL.get();
	}

	public static void removeName() {
		TaskContext.RUNNABLE_NAME_THREAD_LOCAL.remove();

	}

}
