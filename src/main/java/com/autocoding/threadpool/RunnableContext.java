package com.autocoding.threadpool;

/**
 * RunnableId 线程上下文
 *
 * @ClassName: MonitoredThreadPoolExecutor
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:10:48 PM
 */
public class RunnableContext {
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
		RunnableContext.RUNNABLE_ID_THREAD_LOCAL.set(id);
	}

	public static String getId() {
		return RunnableContext.RUNNABLE_ID_THREAD_LOCAL.get();
	}

	public static void removeId() {
		RunnableContext.RUNNABLE_ID_THREAD_LOCAL.remove();

	}

	public static void setName(String name) {
		RunnableContext.RUNNABLE_NAME_THREAD_LOCAL.set(name);
	}

	public static String getName() {
		return RunnableContext.RUNNABLE_NAME_THREAD_LOCAL.get();
	}

	public static void removeName() {
		RunnableContext.RUNNABLE_NAME_THREAD_LOCAL.remove();

	}

}
